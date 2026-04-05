package com.loopy.android.data.midi;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0015\b\u0007\u0018\u0000 :2\u00020\u0001:\u0001:B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010'\u001a\u00020 J\u0006\u0010(\u001a\u00020 J\u001e\u0010)\u001a\u00020 2\u0006\u0010*\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020\u001f2\u0006\u0010,\u001a\u00020\u001fJ \u0010-\u001a\u00020 2\u0006\u0010*\u001a\u00020\u001f2\u0006\u0010.\u001a\u00020\u001f2\b\b\u0002\u0010/\u001a\u00020\u001fJ\u001e\u00100\u001a\u00020 2\u0006\u0010*\u001a\u00020\u001f2\u0006\u0010.\u001a\u00020\u001f2\u0006\u0010/\u001a\u00020\u001fJ\u000e\u00101\u001a\u00020\f2\u0006\u00102\u001a\u00020\bJ\u0016\u00103\u001a\u00020 2\u0006\u0010*\u001a\u00020\u001f2\u0006\u00104\u001a\u00020\u001fJ\u0006\u00105\u001a\u00020 J\"\u00106\u001a\u00020 2\u0006\u00107\u001a\u00020\u001e2\b\b\u0002\u00108\u001a\u00020\u001f2\b\b\u0002\u00109\u001a\u00020\u001fR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R4\u0010\u001c\u001a\u001c\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020 \u0018\u00010\u001dX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u000e\u0010%\u001a\u00020&X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006;"}, d2 = {"Lcom/loopy/android/data/midi/LoopyMidiManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_availableDevices", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Landroid/media/midi/MidiDeviceInfo;", "_connectedDeviceName", "", "_isConnected", "", "availableDevices", "Lkotlinx/coroutines/flow/StateFlow;", "getAvailableDevices", "()Lkotlinx/coroutines/flow/StateFlow;", "connectedDeviceName", "getConnectedDeviceName", "isConnected", "midiDevice", "Landroid/media/midi/MidiDevice;", "midiInputPort", "Landroid/media/midi/MidiInputPort;", "midiManager", "Landroid/media/midi/MidiManager;", "midiOutputPort", "Landroid/media/midi/MidiOutputPort;", "onMidiMessage", "Lkotlin/Function3;", "", "", "", "getOnMidiMessage", "()Lkotlin/jvm/functions/Function3;", "setOnMidiMessage", "(Lkotlin/jvm/functions/Function3;)V", "receiveReceiver", "Landroid/media/midi/MidiReceiver;", "allNotesOff", "closeDevice", "controlChange", "channel", "controller", "value", "noteOff", "note", "velocity", "noteOn", "openDevice", "deviceInfo", "programChange", "program", "refreshDevices", "sendMidi", "data", "offset", "count", "Companion", "app_debug"})
public final class LoopyMidiManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.media.midi.MidiManager midiManager = null;
    @org.jetbrains.annotations.Nullable()
    private android.media.midi.MidiDevice midiDevice;
    @org.jetbrains.annotations.Nullable()
    private android.media.midi.MidiInputPort midiInputPort;
    @org.jetbrains.annotations.Nullable()
    private android.media.midi.MidiOutputPort midiOutputPort;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _connectedDeviceName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> connectedDeviceName = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<android.media.midi.MidiDeviceInfo>> _availableDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<android.media.midi.MidiDeviceInfo>> availableDevices = null;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function3<? super byte[], ? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onMidiMessage;
    @org.jetbrains.annotations.NotNull()
    private final android.media.midi.MidiReceiver receiveReceiver = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "LoopyMidiManager";
    @org.jetbrains.annotations.NotNull()
    public static final com.loopy.android.data.midi.LoopyMidiManager.Companion Companion = null;
    
    @javax.inject.Inject()
    public LoopyMidiManager(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected() {
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
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function3<byte[], java.lang.Integer, java.lang.Integer, kotlin.Unit> getOnMidiMessage() {
        return null;
    }
    
    public final void setOnMidiMessage(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function3<? super byte[], ? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> p0) {
    }
    
    public final void refreshDevices() {
    }
    
    public final boolean openDevice(@org.jetbrains.annotations.NotNull()
    android.media.midi.MidiDeviceInfo deviceInfo) {
        return false;
    }
    
    public final void closeDevice() {
    }
    
    /**
     * Send MIDI message to the connected device via the input port
     */
    public final void sendMidi(@org.jetbrains.annotations.NotNull()
    byte[] data, int offset, int count) {
    }
    
    public final void noteOn(int channel, int note, int velocity) {
    }
    
    public final void noteOff(int channel, int note, int velocity) {
    }
    
    public final void programChange(int channel, int program) {
    }
    
    public final void controlChange(int channel, int controller, int value) {
    }
    
    public final void allNotesOff() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/loopy/android/data/midi/LoopyMidiManager$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}