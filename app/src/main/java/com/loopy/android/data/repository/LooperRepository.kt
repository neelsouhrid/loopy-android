package com.loopy.android.data.repository

import com.loopy.android.data.datastore.SessionDataStore
import com.loopy.android.data.midi.LoopyMidiManager
import com.loopy.android.data.audio.AudioEngine
import com.loopy.android.data.export.ExportManager
import com.loopy.android.domain.model.LooperMode
import com.loopy.android.domain.model.MidiEvent
import com.loopy.android.domain.model.MidiEventType
import com.loopy.android.domain.model.PlaybackState
import com.loopy.android.domain.model.Session
import com.loopy.android.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LooperRepository @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val midiManager: LoopyMidiManager,
    private val audioEngine: AudioEngine,
    private val exportManager: ExportManager
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Sessions
    val sessions = sessionDataStore.sessions
    val currentSessionId = sessionDataStore.currentSessionId

    // MIDI connection state
    val isMidiConnected: StateFlow<Boolean> = midiManager.isConnected
    val connectedDeviceName: StateFlow<String?> = midiManager.connectedDeviceName
    val availableDevices = midiManager.availableDevices
    
    fun refreshMidiDevices() = midiManager.refreshDevices()
    
    fun connectMidiDevice(deviceInfo: android.media.midi.MidiDeviceInfo) {
        midiManager.openDevice(deviceInfo)
    }
    
    // Current session state
    private val _currentSession = MutableStateFlow<Session?>(null)
    val currentSession: StateFlow<Session?> = _currentSession.asStateFlow()

    // Looper state
    private val _selectedTrackIndex = MutableStateFlow(0)
    val selectedTrackIndex: StateFlow<Int> = _selectedTrackIndex.asStateFlow()
    
    private val _mode = MutableStateFlow(LooperMode.RECORD)
    val mode: StateFlow<LooperMode> = _mode.asStateFlow()
    
    private val _playbackState = MutableStateFlow(PlaybackState.STOPPED)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    // Recording state
    private var recordingStartTime = 0L
    private var recordingEvents = mutableListOf<MidiEvent>()
    private var playbackJob: Job? = null
    private var currentProgramChange: Int? = null
    private var lastMidiStatus: Int = 0

    private var globalPlayheadMicros: Long = 0L

    private val undoStack = mutableListOf<Session>()
    private val redoStack = mutableListOf<Session>()
    
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()
    
    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()
    
    private fun saveStateToUndoStack() {
        _currentSession.value?.let { current ->
            undoStack.add(current)
            if (undoStack.size > 10) {
                undoStack.removeAt(0)
            }
            redoStack.clear()
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = redoStack.isNotEmpty()
        }
    }
    
    fun undo() {
        if (undoStack.isNotEmpty() && _playbackState.value == PlaybackState.STOPPED) {
            _currentSession.value?.let { current ->
                redoStack.add(current)
                if (redoStack.size > 10) redoStack.removeAt(0)
            }
            
            val previousState = undoStack.removeAt(undoStack.lastIndex)
            _currentSession.value = previousState
            
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = redoStack.isNotEmpty()
            
            scope.launch { sessionDataStore.saveSessions(listOf(previousState)) }
        }
    }
    
    fun redo() {
        if (redoStack.isNotEmpty() && _playbackState.value == PlaybackState.STOPPED) {
            _currentSession.value?.let { current ->
                undoStack.add(current)
                if (undoStack.size > 10) undoStack.removeAt(0)
            }
            
            val nextState = redoStack.removeAt(redoStack.lastIndex)
            _currentSession.value = nextState
            
            _canUndo.value = undoStack.isNotEmpty()
            _canRedo.value = redoStack.isNotEmpty()
            
            scope.launch { sessionDataStore.saveSessions(listOf(nextState)) }
        }
    }

    fun resetPlayback() {
        if (_playbackState.value == PlaybackState.RECORDING) return
        stop()
        globalPlayheadMicros = 0L
        _playbackPosition.value = 0f
    }
    private val _midiIndicator = MutableStateFlow(false)
    val midiIndicator: StateFlow<Boolean> = _midiIndicator.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0f)
    val playbackPosition: StateFlow<Float> = _playbackPosition.asStateFlow()

    private val _currentNotesDisplay = MutableStateFlow<String?>(null)
    val currentNotesDisplay: StateFlow<String?> = _currentNotesDisplay.asStateFlow()

    private val currentlyPressedNotes = mutableSetOf<Int>()

    // MIDI input callback
    init {
        midiManager.onMidiMessage = { data, offset, count ->
            handleMidiInput(data, offset, count)
        }
        
        // Initialize audio engine
        audioEngine.initialize()
    }

    suspend fun initialize() {
        // Load sessions
        val loadedSessions = sessions.first()
        val sessionId = currentSessionId.first()
        
        if (loadedSessions.isEmpty()) {
            // Create default session
            val defaultSession = Session(name = "Session 1")
            sessionDataStore.saveSessions(listOf(defaultSession))
            sessionDataStore.setCurrentSession(defaultSession.id)
            _currentSession.value = defaultSession
        } else {
            val session = if (sessionId != null) {
                loadedSessions.find { it.id == sessionId } ?: loadedSessions.first()
            } else {
                loadedSessions.first()
            }
            sessionDataStore.setCurrentSession(session.id)
            _currentSession.value = session
        }
        
        // Load audio muted state
        _isMuted.value = sessionDataStore.audioMuted.first()
    }

    fun selectTrack(index: Int) {
        if (index in 0..9) {
            _selectedTrackIndex.value = index
        }
    }

    fun setMode(newMode: LooperMode) {
        if (_playbackState.value == PlaybackState.STOPPED) {
            if (newMode == LooperMode.RECORD || newMode == LooperMode.OVERDUB) {
                globalPlayheadMicros = 0L
                _playbackPosition.value = 0f
            }
            _mode.value = newMode
        }
    }

    // Keep for backward compatibility if needed, or remove and update calls
    fun toggleMode() {
        if (_playbackState.value == PlaybackState.STOPPED) {
            _mode.value = when (_mode.value) {
                LooperMode.RECORD -> LooperMode.PLAY
                LooperMode.PLAY -> LooperMode.OVERDUB
                LooperMode.OVERDUB -> {
                    globalPlayheadMicros = 0L
                    _playbackPosition.value = 0f
                    LooperMode.RECORD
                }
            }
        }
    }

    fun startStop() {
        when (_playbackState.value) {
            PlaybackState.STOPPED -> {
                if (_mode.value == LooperMode.RECORD || _mode.value == LooperMode.OVERDUB) {
                    startRecording()
                } else {
                    startPlayback()
                }
            }
            PlaybackState.PLAYING, PlaybackState.RECORDING -> {
                stop()
            }
        }
    }

    private fun startRecording() {
        globalPlayheadMicros = 0L
        _playbackPosition.value = 0f
        
        _playbackState.value = PlaybackState.RECORDING
        recordingStartTime = System.nanoTime() / 1000 // microseconds
        recordingEvents.clear()
        
        val session = _currentSession.value ?: return
        val track = session.tracks[_selectedTrackIndex.value]
        
        if (_mode.value == LooperMode.OVERDUB && track.isNotEmpty) {
            // In overdub mode, keep existing events
            recordingEvents.addAll(track.events)
            
            // Play all non-empty tracks including the one we are overdubbing
            val nonEmptyTracks = session.tracks.filter { it.isNotEmpty }
            if (nonEmptyTracks.isNotEmpty()) {
                playTracks(nonEmptyTracks)
            }
        } else {
            // Standard record mode: clear the track and play only backing tracks
            val backingTracks = session.tracks.filterIndexed { i, t -> i != _selectedTrackIndex.value && t.isNotEmpty }
            if (backingTracks.isNotEmpty()) {
                playTracks(backingTracks)
            }
        }
    }

    private fun startPlayback() {
        val session = _currentSession.value ?: return
        val nonEmptyTracks = session.tracks.filter { it.isNotEmpty }
        
        if (nonEmptyTracks.isEmpty()) return
        
        _playbackState.value = PlaybackState.PLAYING
        playTracks(nonEmptyTracks)
    }

    private fun playTracks(tracks: List<Track>) {
        val longestDuration = tracks.maxOfOrNull { it.durationMicros } ?: return
        if (longestDuration == 0L) return
        
        // Collect all events with timestamps as AudioEngine.PlaybackEvent
        val allEvents = mutableListOf<AudioEngine.PlaybackEvent>()
        
        tracks.forEach { track ->
            track.events.forEach { event ->
                if (event.type == MidiEventType.NOTE_ON && event.note != null && event.velocity != null) {
                    allEvents.add(
                        AudioEngine.PlaybackEvent(
                            note = event.note,
                            velocity = event.velocity,
                            timestampMicros = event.timestampMicros
                        )
                    )
                }
            }
        }
        
        // Also send to MIDI keyboard and trigger AudioEngine samples
        playbackJob = scope.launch {
            var lastLoopPosition = -1L
            var programChangesSent = false
            var lastCurrentTime = System.nanoTime() / 1000
            
            while (_playbackState.value == PlaybackState.PLAYING || _playbackState.value == PlaybackState.RECORDING) {
                // Track current session tempo to scale playback length
                val currentTempo = _currentSession.value?.tempo ?: 120
                val tempoRatio = currentTempo.toDouble() / 120.0
                val scaledDuration = (longestDuration / tempoRatio).toLong()
                
                val currentTime = System.nanoTime() / 1000
                val delta = currentTime - lastCurrentTime
                lastCurrentTime = currentTime
                globalPlayheadMicros += delta
                
                val loopPosition = globalPlayheadMicros % Math.max(1L, scaledDuration)
                
                _playbackPosition.value = (loopPosition.toFloat() / Math.max(1L, scaledDuration).toFloat())
                
                if (_playbackState.value == PlaybackState.RECORDING && longestDuration > 0) {
                    val actualElapsed = currentTime - recordingStartTime
                    if (actualElapsed >= scaledDuration) {
                        scope.launch(Dispatchers.Main) { startStop() }
                    }
                }
                
                // Send program changes (tone) at start of each loop
                if (!programChangesSent || loopPosition < lastLoopPosition) {
                    tracks.forEach { track ->
                        track.programChange?.let { program ->
                            midiManager.programChange(track.id, program)
                        }
                    }
                    programChangesSent = true
                }
                
                // Check for loop restart
                if (loopPosition < lastLoopPosition) {
                    // Loop restarted - send all notes off to prevent stuck notes
                    midiManager.allNotesOff()
                }
                
                // Check which notes should be playing at this precise delta slice
                tracks.forEach { track ->
                    if (track.isMuted) return@forEach
                    
                    if (_playbackState.value == PlaybackState.RECORDING && track.id == _selectedTrackIndex.value && _mode.value != LooperMode.OVERDUB) {
                        return@forEach // Skip playing the actively recording track unless in overdub
                    }
                    
                    track.events.forEach { event ->
                        val eventTime = (event.timestampMicros / tempoRatio).toLong()
                        
                        val shouldTrigger = if (lastLoopPosition == -1L) {
                            eventTime <= loopPosition
                        } else if (loopPosition >= lastLoopPosition) {
                            eventTime > lastLoopPosition && eventTime <= loopPosition
                        } else {
                            // Wrapped around
                            eventTime > lastLoopPosition || eventTime <= loopPosition
                        }
                        
                        if (shouldTrigger) {
                            if (event.type == MidiEventType.NOTE_ON && event.note != null && event.velocity != null) {
                                midiManager.noteOn(track.id, event.note, event.velocity)
                                audioEngine.playNote(event.note, event.velocity)
                            } else if (event.type == MidiEventType.NOTE_OFF && event.note != null) {
                                midiManager.noteOff(track.id, event.note, event.velocity ?: 0)
                            }
                        }
                    }
                }
                
                lastLoopPosition = loopPosition
                delay(10) // Check frequently for accurate playback without stutter
            }
        }
    }

    private fun stop() {
        _playbackState.value = PlaybackState.STOPPED
        
        // Stop audio
        audioEngine.stopPlayback()
        
        // Stop playback job
        playbackJob?.cancel()
        
        // Send MIDI panic
        midiManager.allNotesOff()
        
        // If was recording or overdubbing, save the recording
        if ((_mode.value == LooperMode.RECORD || _mode.value == LooperMode.OVERDUB) && recordingEvents.isNotEmpty()) {
            saveRecording()
        }
        
        // Save session
        scope.launch {
            _currentSession.value?.let { session ->
                sessionDataStore.saveSessions(listOf(session))
            }
        }
    }

    private fun updateNotesDisplay() {
        if (currentlyPressedNotes.isEmpty()) {
            _currentNotesDisplay.value = null
            return
        }
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val display = currentlyPressedNotes.sorted().joinToString(" • ") { note ->
            "${noteNames[note % 12]}${note / 12 - 1}"
        }
        _currentNotesDisplay.value = display
    }

    private fun triggerMidiIndicator() {
        scope.launch {
            _midiIndicator.value = true
            delay(100)
            _midiIndicator.value = false
        }
    }

    private fun handleMidiInput(data: ByteArray, offset: Int, count: Int) {
        if (data.isEmpty() || count == 0) return
        
        triggerMidiIndicator()
        
        val currentTime = System.nanoTime() / 1000 // microseconds
        var timestamp = currentTime - recordingStartTime
        
        // In overdub mode, wrap timestamp to loop duration
        if (_mode.value == LooperMode.OVERDUB) {
            val session = _currentSession.value
            val longestDuration = session?.longestTrackDuration ?: 0L
            if (longestDuration > 0) {
                timestamp = timestamp % longestDuration
            }
        }
        
        var i = offset
        val end = offset + count
        
        while (i < end) {
            val byte = data[i].toInt() and 0xFF
            var status = byte
            
            if (byte >= 0x80) { // New Status Byte
                lastMidiStatus = byte
                status = byte
                i++
            } else {
                // Running Status
                status = lastMidiStatus
            }
            
            if (status == 0) { i++; continue } // Invalid state
            
            val messageType = status and 0xF0
            val channel = status and 0x0F
            
            val dataBytesNeeded = when(messageType) {
                0x90, 0x80, 0xB0, 0xE0 -> 2
                0xC0, 0xD0 -> 1
                else -> 0
            }
            
            if (dataBytesNeeded == 2 && i + 1 < end) {
                val d1 = data[i].toInt() and 0xFF
                val d2 = data[i+1].toInt() and 0xFF
                i += 2
                
                if (messageType == 0x90) { // Note On
                    if (d2 > 0) {
                        currentlyPressedNotes.add(d1)
                        updateNotesDisplay()
                        if (_playbackState.value == PlaybackState.RECORDING) {
                            recordingEvents.add(MidiEvent(MidiEventType.NOTE_ON, channel, d1, d2, timestampMicros = timestamp))
                        }
                        audioEngine.playNote(d1, d2)
                        midiManager.noteOn(_selectedTrackIndex.value, d1, d2)
                    } else { // Velocity 0 == Note Off
                        currentlyPressedNotes.remove(d1)
                        updateNotesDisplay()
                        if (_playbackState.value == PlaybackState.RECORDING) {
                            recordingEvents.add(MidiEvent(MidiEventType.NOTE_OFF, channel, d1, 0, timestampMicros = timestamp))
                        }
                        midiManager.noteOff(_selectedTrackIndex.value, d1, 0)
                    }
                } else if (messageType == 0x80) { // Note Off
                    currentlyPressedNotes.remove(d1)
                    updateNotesDisplay()
                    if (_playbackState.value == PlaybackState.RECORDING) {
                        recordingEvents.add(MidiEvent(MidiEventType.NOTE_OFF, channel, d1, d2, timestampMicros = timestamp))
                    }
                    midiManager.noteOff(_selectedTrackIndex.value, d1, d2)
                }
            } else if (dataBytesNeeded == 1 && i < end) {
                val d1 = data[i].toInt() and 0xFF
                if (messageType == 0xC0) { // Program Change
                    currentProgramChange = d1
                    midiManager.programChange(_selectedTrackIndex.value, d1)
                }
                i += 1
            } else {
                break 
            }
        }
    }


    private fun saveRecording() {
        val session = _currentSession.value ?: return
        
        saveStateToUndoStack()
        
        // Calculate the elapsed recorded time
        val elapsed = (System.nanoTime() / 1000) - recordingStartTime
        val currentTempoRatio = session.tempo.toDouble() / 120.0
        
        // Scale the events back to 120 BPM absolute time to store in the array uniformly
        val storedEvents = recordingEvents.map { it.copy(timestampMicros = (it.timestampMicros * currentTempoRatio).toLong()) }
        
        var combinedEvents = storedEvents
        // If overdubbing, safely merge the new events with the existing ones!
        if (_mode.value == LooperMode.OVERDUB) {
            val existingEvents = session.tracks.find { it.id == _selectedTrackIndex.value }?.events ?: emptyList()
            combinedEvents = (existingEvents + storedEvents).sortedBy { it.timestampMicros }
        }
        
        // Determine track duration: use the existing longest duration (so loops remain synced),
        // or if it's the first track, leave it at exact elapsed time allowing the user to precisely set it
        val duration = if (session.longestTrackDuration > 0) {
            session.longestTrackDuration
        } else {
            val scaledElapsed = (elapsed * currentTempoRatio).toLong()
            scaledElapsed
        }
        
        val updatedTrack = Track(
            id = _selectedTrackIndex.value,
            events = combinedEvents,
            programChange = currentProgramChange,
            durationMicros = duration
        )
        
        val updatedTracks = session.tracks.toMutableList()
        updatedTracks[_selectedTrackIndex.value] = updatedTrack
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun clearTrack(index: Int) {
        val session = _currentSession.value ?: return
        
        saveStateToUndoStack()
        
        val updatedTrack = Track(id = index)
        val updatedTracks = session.tracks.toMutableList()
        updatedTracks[index] = updatedTrack
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            _currentSession.value?.let { sessionDataStore.saveSessions(listOf(it)) }
        }
    }

    fun clearAllTracks() {
        val session = _currentSession.value ?: return
        
        saveStateToUndoStack()
        
        resetPlayback()
        
        val updatedTracks = (0..9).map { Track(id = it) }
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            _currentSession.value?.let { sessionDataStore.saveSessions(listOf(it)) }
        }
    }

    fun createSession(name: String) {
        scope.launch {
            val newSession = Session(name = name)
            val allSessions = sessions.first().toMutableList()
            allSessions.add(newSession)
            
            sessionDataStore.saveSessions(allSessions)
            sessionDataStore.setCurrentSession(newSession.id)
            _currentSession.value = newSession
        }
    }

    fun switchSession(sessionId: String) {
        scope.launch {
            val allSessions = sessions.first()
            val session = allSessions.find { it.id == sessionId }
            
            if (session != null) {
                sessionDataStore.setCurrentSession(sessionId)
                _currentSession.value = session
            }
        }
    }

    fun toggleTrackMute(index: Int) {
        val session = _currentSession.value ?: return
        val currentTrack = session.tracks[index]
        val updatedTrack = currentTrack.copy(isMuted = !currentTrack.isMuted)
        
        val updatedTracks = session.tracks.toMutableList()
        updatedTracks[index] = updatedTrack
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            _currentSession.value?.let { sessionDataStore.saveSessions(listOf(it)) }
        }
    }

    fun updateTrackMetadata(index: Int, name: String?, color: Long?, emoji: String?) {
        val session = _currentSession.value ?: return
        val currentTrack = session.tracks[index]
        val updatedTrack = currentTrack.copy(name = name, color = color, emoji = emoji)
        
        val updatedTracks = session.tracks.toMutableList()
        updatedTracks[index] = updatedTrack
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            _currentSession.value?.let { sessionDataStore.saveSessions(listOf(it)) }
        }
    }

    fun renameSession(sessionId: String, newName: String) {
        scope.launch {
            val allSessions = sessions.first().toMutableList()
            val index = allSessions.indexOfFirst { it.id == sessionId }
            
            if (index >= 0) {
                allSessions[index] = allSessions[index].copy(
                    name = newName,
                    updatedAt = System.currentTimeMillis()
                )
                sessionDataStore.saveSessions(allSessions)
                
                if (sessionId == _currentSession.value?.id) {
                    _currentSession.value = allSessions[index]
                }
            }
        }
    }

    fun setTempo(newTempo: Int) {
        val session = _currentSession.value ?: return
        if (newTempo in 40..240) {
            _currentSession.value = session.copy(
                tempo = newTempo,
                updatedAt = System.currentTimeMillis()
            )
            scope.launch {
                sessionDataStore.saveSessions(listOf(_currentSession.value!!))
            }
        }
    }

    fun deleteSession(sessionId: String) {
        scope.launch {
            val allSessions = sessions.first().toMutableList()
            allSessions.removeAll { it.id == sessionId }
            
            if (allSessions.isEmpty()) {
                // Create new default session
                val defaultSession = Session(name = "Session 1")
                allSessions.add(defaultSession)
                sessionDataStore.setCurrentSession(defaultSession.id)
                _currentSession.value = defaultSession
            } else if (sessionId == _currentSession.value?.id) {
                sessionDataStore.setCurrentSession(allSessions.first().id)
                _currentSession.value = allSessions.first()
            }
            
            sessionDataStore.saveSessions(allSessions)
        }
    }

    suspend fun exportMidi(merged: Boolean): List<String> {
        val session = _currentSession.value ?: return emptyList()
        return exportManager.exportMidi(session, merged)
    }

    suspend fun exportAudio(): String? {
        val session = _currentSession.value ?: return null
        return exportManager.exportAudio(session)
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        audioEngine.isMuted = _isMuted.value
        
        scope.launch {
            sessionDataStore.setAudioMuted(_isMuted.value)
        }
    }

    fun release() {
        audioEngine.release()
        midiManager.closeDevice()
    }
}