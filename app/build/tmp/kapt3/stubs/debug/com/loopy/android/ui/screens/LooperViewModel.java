package com.loopy.android.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material.icons.outlined.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.lifecycle.ViewModel;
import com.loopy.android.data.repository.LooperRepository;
import com.loopy.android.domain.model.LooperMode;
import com.loopy.android.domain.model.PlaybackState;
import com.loopy.android.domain.model.Session;
import com.loopy.android.domain.model.Track;
import com.loopy.android.ui.theme.*;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0012\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010*\u001a\u00020+J\u000e\u0010,\u001a\u00020+2\u0006\u0010-\u001a\u00020&J\u000e\u0010.\u001a\u00020+2\u0006\u0010/\u001a\u00020\bJ\u000e\u00100\u001a\u00020+2\u0006\u00101\u001a\u00020\u0011J\u000e\u00102\u001a\u00020+2\u0006\u00103\u001a\u00020\u0011J\u0006\u00104\u001a\u000205J\u000e\u00106\u001a\u0002052\u0006\u00107\u001a\u00020\fJ\b\u00108\u001a\u00020+H\u0014J\u0006\u00109\u001a\u00020+J\u0006\u0010:\u001a\u00020+J\u0016\u0010;\u001a\u00020+2\u0006\u00103\u001a\u00020\u00112\u0006\u00101\u001a\u00020\u0011J\u0006\u0010<\u001a\u00020+J\u000e\u0010=\u001a\u00020+2\u0006\u0010-\u001a\u00020&J\u000e\u0010>\u001a\u00020+2\u0006\u0010?\u001a\u00020\u001dJ\u000e\u0010@\u001a\u00020+2\u0006\u0010A\u001a\u00020&J\u0006\u0010B\u001a\u00020+J\u000e\u0010C\u001a\u00020+2\u0006\u00103\u001a\u00020\u0011J\u0006\u0010D\u001a\u00020+J\u0006\u0010E\u001a\u00020+J\u0006\u0010F\u001a\u00020+R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\nR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\nR\u0019\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\nR\u0019\u0010\u0013\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\nR\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\nR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\nR\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\nR\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\nR\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\nR\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\nR\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b'\u0010\nR\u001d\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\n\u00a8\u0006G"}, d2 = {"Lcom/loopy/android/ui/screens/LooperViewModel;", "Landroidx/lifecycle/ViewModel;", "looperRepository", "Lcom/loopy/android/data/repository/LooperRepository;", "(Lcom/loopy/android/data/repository/LooperRepository;)V", "availableDevices", "Lkotlinx/coroutines/flow/StateFlow;", "", "Landroid/media/midi/MidiDeviceInfo;", "getAvailableDevices", "()Lkotlinx/coroutines/flow/StateFlow;", "canRedo", "", "getCanRedo", "canUndo", "getCanUndo", "connectedDeviceName", "", "getConnectedDeviceName", "currentNotesDisplay", "getCurrentNotesDisplay", "currentSession", "Lcom/loopy/android/domain/model/Session;", "getCurrentSession", "isMidiConnected", "isMuted", "midiIndicator", "getMidiIndicator", "mode", "Lcom/loopy/android/domain/model/LooperMode;", "getMode", "playbackPosition", "", "getPlaybackPosition", "playbackState", "Lcom/loopy/android/domain/model/PlaybackState;", "getPlaybackState", "selectedTrackIndex", "", "getSelectedTrackIndex", "sessions", "getSessions", "clearAllTracks", "", "clearTrack", "index", "connectMidiDevice", "deviceInfo", "createSession", "name", "deleteSession", "id", "exportAudio", "Lkotlinx/coroutines/Job;", "exportMidi", "merged", "onCleared", "redo", "refreshMidiDevices", "renameSession", "resetPlayback", "selectTrack", "setMode", "newMode", "setTempo", "tempo", "startStop", "switchSession", "toggleMode", "toggleMute", "undo", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class LooperViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.data.repository.LooperRepository looperRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.Session> currentSession = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> selectedTrackIndex = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.LooperMode> mode = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.loopy.android.domain.model.PlaybackState> playbackState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMuted = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.loopy.android.domain.model.Session>> sessions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isMidiConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> connectedDeviceName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<android.media.midi.MidiDeviceInfo>> availableDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> midiIndicator = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentNotesDisplay = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Float> playbackPosition = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> canUndo = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> canRedo = null;
    
    @javax.inject.Inject()
    public LooperViewModel(@org.jetbrains.annotations.NotNull()
    com.loopy.android.data.repository.LooperRepository looperRepository) {
        super();
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
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.loopy.android.domain.model.Session>> getSessions() {
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
    
    public final void selectTrack(int index) {
    }
    
    public final void setMode(@org.jetbrains.annotations.NotNull()
    com.loopy.android.domain.model.LooperMode newMode) {
    }
    
    public final void setTempo(int tempo) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getMidiIndicator() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentNotesDisplay() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Float> getPlaybackPosition() {
        return null;
    }
    
    public final void toggleMode() {
    }
    
    public final void startStop() {
    }
    
    public final void resetPlayback() {
    }
    
    public final void undo() {
    }
    
    public final void redo() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getCanUndo() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getCanRedo() {
        return null;
    }
    
    public final void clearTrack(int index) {
    }
    
    public final void clearAllTracks() {
    }
    
    public final void toggleMute() {
    }
    
    public final void createSession(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void switchSession(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    public final void renameSession(@org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
    
    public final void deleteSession(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job exportMidi(boolean merged) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job exportAudio() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}