package com.loopy.android.data.repository

import com.loopy.android.data.datastore.SessionDataStore
import com.loopy.android.data.midi.MidiManager
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
    private val midiManager: MidiManager,
    private val audioEngine: AudioEngine,
    private val exportManager: ExportManager
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Sessions
    val sessions = sessionDataStore.sessions
    val currentSessionId = sessionDataStore.currentSessionId
    
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

    fun toggleMode() {
        if (_playbackState.value == PlaybackState.STOPPED) {
            _mode.value = when (_mode.value) {
                LooperMode.RECORD -> LooperMode.PLAY
                LooperMode.PLAY -> LooperMode.RECORD
            }
        }
    }

    fun startStop() {
        when (_playbackState.value) {
            PlaybackState.STOPPED -> {
                if (_mode.value == LooperMode.RECORD) {
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
        _playbackState.value = PlaybackState.RECORDING
        recordingStartTime = System.nanoTime() / 1000 // microseconds
        recordingEvents.clear()
        
        // If not first track, start backing tracks
        val session = _currentSession.value ?: return
        val track = session.tracks[_selectedTrackIndex.value]
        
        if (track.isNotEmpty) {
            // Play backing tracks
            playTracks(session.tracks.filterIndexed { i, t -> i != _selectedTrackIndex.value && t.isNotEmpty })
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
        
        // Collect all events with absolute timestamps
        val allEvents = mutableListOf<Pair<Int, Int>>() // note, velocity
        
        tracks.forEach { track ->
            track.events.forEach { event ->
                if (event.type == MidiEventType.NOTE_ON && event.note != null && event.velocity != null) {
                    allEvents.add(Pair(event.note, event.velocity))
                }
            }
        }
        
        // Start audio playback
        audioEngine.startPlayback(
            events = allEvents,
            startTimeMicros = System.nanoTime() / 1000,
            loopDurationMicros = longestDuration
        )
        
        // Also send to MIDI keyboard
        playbackJob = scope.launch {
            var lastLoopPosition = 0L
            var programChangesSent = false
            
            while (_playbackState.value == PlaybackState.PLAYING) {
                val currentTime = System.nanoTime() / 1000
                val loopPosition = currentTime % longestDuration
                
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
                
                // Check which notes should be playing at this position
                tracks.forEach { track ->
                    track.events.forEach { event ->
                        if (event.type == MidiEventType.NOTE_ON && event.note != null && event.velocity != null) {
                            val eventTime = event.timestampMicros
                            val noteDuration = 100000 // 100ms default note duration
                            
                            if (loopPosition >= eventTime && loopPosition < eventTime + noteDuration) {
                                midiManager.noteOn(track.id, event.note, event.velocity)
                            } else if (loopPosition < lastLoopPosition && currentTime < eventTime + noteDuration) {
                                // Was playing before loop restart
                            }
                        }
                    }
                }
                
                lastLoopPosition = loopPosition
                delay(50) // Check every 50ms
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
        
        // If was recording, save the recording
        if (_mode.value == LooperMode.RECORD && recordingEvents.isNotEmpty()) {
            saveRecording()
        }
        
        // Save session
        scope.launch {
            _currentSession.value?.let { session ->
                sessionDataStore.saveSessions(listOf(session))
            }
        }
    }

    private fun handleMidiInput(data: ByteArray, offset: Int, count: Int) {
        if (_playbackState.value != PlaybackState.RECORDING) return
        if (data.isEmpty()) return
        
        val status = data[offset].toInt() and 0xFF
        val messageType = status and 0xF0
        val channel = status and 0x0F
        
        val currentTime = System.nanoTime() / 1000 // microseconds
        val timestamp = currentTime - recordingStartTime
        
        when (messageType) {
            0x90 -> { // Note On
                if (count >= 3) {
                    val note = data[offset + 1].toInt() and 0xFF
                    val velocity = data[offset + 2].toInt() and 0xFF
                    
                    if (velocity > 0) {
                        val event = MidiEvent(
                            type = MidiEventType.NOTE_ON,
                            channel = channel,
                            note = note,
                            velocity = velocity,
                            timestampMicros = timestamp
                        )
                        recordingEvents.add(event)
                        
                        // Also forward to keyboard
                        midiManager.noteOn(_selectedTrackIndex.value, note, velocity)
                    } else {
                        // Note Off (velocity 0 = note off)
                        val event = MidiEvent(
                            type = MidiEventType.NOTE_OFF,
                            channel = channel,
                            note = note,
                            velocity = 0,
                            timestampMicros = timestamp
                        )
                        recordingEvents.add(event)
                        midiManager.noteOff(_selectedTrackIndex.value, note, 0)
                    }
                }
            }
            0x80 -> { // Note Off
                if (count >= 3) {
                    val note = data[offset + 1].toInt() and 0xFF
                    val velocity = data[offset + 2].toInt() and 0xFF
                    
                    val event = MidiEvent(
                        type = MidiEventType.NOTE_OFF,
                        channel = channel,
                        note = note,
                        velocity = velocity,
                        timestampMicros = timestamp
                    )
                    recordingEvents.add(event)
                    midiManager.noteOff(_selectedTrackIndex.value, note, velocity)
                }
            }
            0xB0 -> { // Control Change
                if (count >= 3) {
                    val controller = data[offset + 1].toInt() and 0xFF
                    val value = data[offset + 2].toInt() and 0xFF
                    
                    val event = MidiEvent(
                        type = MidiEventType.CONTROL_CHANGE,
                        channel = channel,
                        value = value,
                        timestampMicros = timestamp
                    )
                    recordingEvents.add(event)
                    
                    // Forward CC to keyboard
                    midiManager.controlChange(_selectedTrackIndex.value, controller, value)
                }
            }
            0xC0 -> { // Program Change
                if (count >= 2) {
                    val program = data[offset + 1].toInt() and 0xFF
                    currentProgramChange = program
                    midiManager.programChange(_selectedTrackIndex.value, program)
                }
            }
        }
    }

    private fun saveRecording() {
        val session = _currentSession.value ?: return
        
        val duration = recordingEvents.maxOfOrNull { it.timestampMicros } ?: 0L
        
        val updatedTrack = Track(
            id = _selectedTrackIndex.value,
            events = recordingEvents.toList(),
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
        
        val updatedTrack = Track(id = index)
        val updatedTracks = session.tracks.toMutableList()
        updatedTracks[index] = updatedTrack
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            sessionDataStore.saveSessions(listOf(_currentSession.value!!))
        }
    }

    fun clearAllTracks() {
        val session = _currentSession.value ?: return
        
        val updatedTracks = (0..9).map { Track(id = it) }
        
        _currentSession.value = session.copy(
            tracks = updatedTracks,
            updatedAt = System.currentTimeMillis()
        )
        
        scope.launch {
            sessionDataStore.saveSessions(listOf(_currentSession.value!!))
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