package com.loopy.android.data.repository;

import com.loopy.android.data.datastore.SessionDataStore;
import com.loopy.android.data.midi.LoopyMidiManager;
import com.loopy.android.data.audio.AudioEngine;
import com.loopy.android.data.export.ExportManager;
import com.loopy.android.domain.model.LooperMode;
import com.loopy.android.domain.model.MidiEvent;
import com.loopy.android.domain.model.MidiEventType;
import com.loopy.android.domain.model.PlaybackState;
import com.loopy.android.domain.model.Session;
import com.loopy.android.domain.model.Track;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0000\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u000f\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u001d\b\u0007\u0018\u00002\u00020\u0001B'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0006\u0010P\u001a\u00020QJ\u000e\u0010R\u001a\u00020Q2\u0006\u0010S\u001a\u00020\u001cJ\u000e\u0010T\u001a\u00020Q2\u0006\u0010U\u001a\u00020 J\u000e\u0010V\u001a\u00020Q2\u0006\u0010W\u001a\u00020\u0010J\u000e\u0010X\u001a\u00020Q2\u0006\u0010Y\u001a\u00020\u0010J\u0010\u0010Z\u001a\u0004\u0018\u00010\u0010H\u0086@\u00a2\u0006\u0002\u0010[J\u001c\u0010\\\u001a\b\u0012\u0004\u0012\u00020\u00100\u001f2\u0006\u0010]\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010^J \u0010_\u001a\u00020Q2\u0006\u0010`\u001a\u00020a2\u0006\u0010b\u001a\u00020\u001c2\u0006\u0010c\u001a\u00020\u001cH\u0002J\u000e\u0010d\u001a\u00020QH\u0086@\u00a2\u0006\u0002\u0010[J\u0016\u0010e\u001a\u00020Q2\f\u0010f\u001a\b\u0012\u0004\u0012\u00020g0\u001fH\u0002J\u0006\u0010h\u001a\u00020QJ\u0006\u0010i\u001a\u00020QJ\u0006\u0010j\u001a\u00020QJ\u0016\u0010k\u001a\u00020Q2\u0006\u0010Y\u001a\u00020\u00102\u0006\u0010l\u001a\u00020\u0010J\u0006\u0010m\u001a\u00020QJ\b\u0010n\u001a\u00020QH\u0002J\b\u0010o\u001a\u00020QH\u0002J\u000e\u0010p\u001a\u00020Q2\u0006\u0010S\u001a\u00020\u001cJ\u000e\u0010q\u001a\u00020Q2\u0006\u0010r\u001a\u00020\u0016J\u000e\u0010s\u001a\u00020Q2\u0006\u0010t\u001a\u00020\u001cJ\b\u0010u\u001a\u00020QH\u0002J\b\u0010v\u001a\u00020QH\u0002J\u0006\u0010w\u001a\u00020QJ\b\u0010x\u001a\u00020QH\u0002J\u000e\u0010y\u001a\u00020Q2\u0006\u0010Y\u001a\u00020\u0010J\u0006\u0010z\u001a\u00020QJ\u0006\u0010{\u001a\u00020QJ\u000e\u0010|\u001a\u00020Q2\u0006\u0010S\u001a\u00020\u001cJ\b\u0010}\u001a\u00020QH\u0002J\u0006\u0010~\u001a\u00020QJ\b\u0010\u007f\u001a\u00020QH\u0002J5\u0010\u0080\u0001\u001a\u00020Q2\u0006\u0010S\u001a\u00020\u001c2\b\u0010W\u001a\u0004\u0018\u00010\u00102\t\u0010\u0081\u0001\u001a\u0004\u0018\u0001062\t\u0010\u0082\u0001\u001a\u0004\u0018\u00010\u0010\u00a2\u0006\u0003\u0010\u0083\u0001R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u001f0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\r0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\"R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\r0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\"R\u0019\u0010'\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\"R\u0019\u0010)\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\"R\u0012\u0010+\u001a\u0004\u0018\u00010\u001cX\u0082\u000e\u00a2\u0006\u0004\n\u0002\u0010,R\u0019\u0010-\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00120\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\"R\u0019\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001000\u00a2\u0006\b\n\u0000\u001a\u0004\b1\u00102R\u0014\u00103\u001a\b\u0012\u0004\u0012\u00020\u001c04X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00105\u001a\u000206X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u00107\u001a\b\u0012\u0004\u0012\u00020\r0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010\"R\u0017\u00108\u001a\b\u0012\u0004\u0012\u00020\r0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\"R\u000e\u00109\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010:\u001a\b\u0012\u0004\u0012\u00020\r0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b;\u0010\"R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010<\u001a\b\u0012\u0004\u0012\u00020\u00160\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u0010\"R\u0010\u0010>\u001a\u0004\u0018\u00010?X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00180\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bA\u0010\"R\u0017\u0010B\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010\"R\u0014\u0010D\u001a\b\u0012\u0004\u0012\u00020F0EX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010G\u001a\u000206X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010H\u001a\b\u0012\u0004\u0012\u00020\u00120EX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010I\u001a\u00020JX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010K\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010\"R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010M\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u001f00\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u00102R\u0014\u0010O\u001a\b\u0012\u0004\u0012\u00020\u00120EX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0084\u0001"}, d2 = {"Lcom/loopy/android/data/repository/LooperRepository;", "", "sessionDataStore", "Lcom/loopy/android/data/datastore/SessionDataStore;", "midiManager", "Lcom/loopy/android/data/midi/LoopyMidiManager;", "audioEngine", "Lcom/loopy/android/data/audio/AudioEngine;", "exportManager", "Lcom/loopy/android/data/export/ExportManager;", "(Lcom/loopy/android/data/datastore/SessionDataStore;Lcom/loopy/android/data/midi/LoopyMidiManager;Lcom/loopy/android/data/audio/AudioEngine;Lcom/loopy/android/data/export/ExportManager;)V", "_canRedo", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_canUndo", "_currentNotesDisplay", "", "_currentSession", "Lcom/loopy/android/domain/model/Session;", "_isMuted", "_midiIndicator", "_mode", "Lcom/loopy/android/domain/model/LooperMode;", "_playbackPosition", "", "_playbackState", "Lcom/loopy/android/domain/model/PlaybackState;", "_selectedTrackIndex", "", "availableDevices", "Lkotlinx/coroutines/flow/StateFlow;", "", "Landroid/media/midi/MidiDeviceInfo;", "getAvailableDevices", "()Lkotlinx/coroutines/flow/StateFlow;", "canRedo", "getCanRedo", "canUndo", "getCanUndo", "connectedDeviceName", "getConnectedDeviceName", "currentNotesDisplay", "getCurrentNotesDisplay", "currentProgramChange", "Ljava/lang/Integer;", "currentSession", "getCurrentSession", "currentSessionId", "Lkotlinx/coroutines/flow/Flow;", "getCurrentSessionId", "()Lkotlinx/coroutines/flow/Flow;", "currentlyPressedNotes", "", "globalPlayheadMicros", "", "isMidiConnected", "isMuted", "lastMidiStatus", "midiIndicator", "getMidiIndicator", "mode", "getMode", "playbackJob", "Lkotlinx/coroutines/Job;", "playbackPosition", "getPlaybackPosition", "playbackState", "getPlaybackState", "recordingEvents", "", "Lcom/loopy/android/domain/model/MidiEvent;", "recordingStartTime", "redoStack", "scope", "Lkotlinx/coroutines/CoroutineScope;", "selectedTrackIndex", "getSelectedTrackIndex", "sessions", "getSessions", "undoStack", "clearAllTracks", "", "clearTrack", "index", "connectMidiDevice", "deviceInfo", "createSession", "name", "deleteSession", "sessionId", "exportAudio", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exportMidi", "merged", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "handleMidiInput", "data", "", "offset", "count", "initialize", "playTracks", "tracks", "Lcom/loopy/android/domain/model/Track;", "redo", "refreshMidiDevices", "release", "renameSession", "newName", "resetPlayback", "saveRecording", "saveStateToUndoStack", "selectTrack", "setMode", "newMode", "setTempo", "newTempo", "startPlayback", "startRecording", "startStop", "stop", "switchSession", "toggleMode", "toggleMute", "toggleTrackMute", "triggerMidiIndicator", "undo", "updateNotesDisplay", "updateTrackMetadata", "color", "emoji", "(ILjava/lang/String;Ljava/lang/Long;Ljava/lang/String;)V", "app_debug"})
public final class LooperRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.data.datastore.SessionDataStore sessionDataStore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.data.midi.LoopyMidiManager midiManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.data.audio.AudioEngine audioEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.data.export.ExportManager exportManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.loopy.android.domain.model.Session>> sessions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> currentSessionId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMidiConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> connectedDeviceName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<android.media.midi.MidiDeviceInfo>> availableDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.loopy.android.domain.model.Session> _currentSession = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.Session> currentSession = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Integer> _selectedTrackIndex = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> selectedTrackIndex = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.loopy.android.domain.model.LooperMode> _mode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.LooperMode> mode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.loopy.android.domain.model.PlaybackState> _playbackState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.PlaybackState> playbackState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isMuted = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMuted = null;
    private long recordingStartTime = 0L;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.loopy.android.domain.model.MidiEvent> recordingEvents;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job playbackJob;
    @org.jetbrains.annotations.Nullable()
    private java.lang.Integer currentProgramChange;
    private int lastMidiStatus = 0;
    private long globalPlayheadMicros = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.loopy.android.domain.model.Session> undoStack = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.loopy.android.domain.model.Session> redoStack = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _canUndo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> canUndo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _canRedo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> canRedo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _midiIndicator = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> midiIndicator = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Float> _playbackPosition = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Float> playbackPosition = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentNotesDisplay = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentNotesDisplay = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Set<java.lang.Integer> currentlyPressedNotes = null;
    
    @javax.inject.Inject()
    public LooperRepository(@org.jetbrains.annotations.NotNull()
    com.loopy.android.data.datastore.SessionDataStore sessionDataStore, @org.jetbrains.annotations.NotNull()
    com.loopy.android.data.midi.LoopyMidiManager midiManager, @org.jetbrains.annotations.NotNull()
    com.loopy.android.data.audio.AudioEngine audioEngine, @org.jetbrains.annotations.NotNull()
    com.loopy.android.data.export.ExportManager exportManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.loopy.android.domain.model.Session>> getSessions() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.lang.String> getCurrentSessionId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMidiConnected() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getConnectedDeviceName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<android.media.midi.MidiDeviceInfo>> getAvailableDevices() {
        return null;
    }
    
    public final void refreshMidiDevices() {
    }
    
    public final void connectMidiDevice(@org.jetbrains.annotations.NotNull()
    android.media.midi.MidiDeviceInfo deviceInfo) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.Session> getCurrentSession() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getSelectedTrackIndex() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.LooperMode> getMode() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.PlaybackState> getPlaybackState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMuted() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getCanUndo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getCanRedo() {
        return null;
    }
    
    private final void saveStateToUndoStack() {
    }
    
    public final void undo() {
    }
    
    public final void redo() {
    }
    
    public final void resetPlayback() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getMidiIndicator() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Float> getPlaybackPosition() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentNotesDisplay() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object initialize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void selectTrack(int index) {
    }
    
    public final void setMode(@org.jetbrains.annotations.NotNull()
    com.loopy.android.domain.model.LooperMode newMode) {
    }
    
    public final void toggleMode() {
    }
    
    public final void startStop() {
    }
    
    private final void startRecording() {
    }
    
    private final void startPlayback() {
    }
    
    private final void playTracks(java.util.List<com.loopy.android.domain.model.Track> tracks) {
    }
    
    private final void stop() {
    }
    
    private final void updateNotesDisplay() {
    }
    
    private final void triggerMidiIndicator() {
    }
    
    private final void handleMidiInput(byte[] data, int offset, int count) {
    }
    
    private final void saveRecording() {
    }
    
    public final void clearTrack(int index) {
    }
    
    public final void clearAllTracks() {
    }
    
    public final void createSession(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void switchSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    public final void toggleTrackMute(int index) {
    }
    
    public final void updateTrackMetadata(int index, @org.jetbrains.annotations.Nullable()
    java.lang.String name, @org.jetbrains.annotations.Nullable()
    java.lang.Long color, @org.jetbrains.annotations.Nullable()
    java.lang.String emoji) {
    }
    
    public final void renameSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull()
    java.lang.String newName) {
    }
    
    public final void setTempo(int newTempo) {
    }
    
    public final void deleteSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object exportMidi(boolean merged, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object exportAudio(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    public final void toggleMute() {
    }
    
    public final void release() {
    }
}