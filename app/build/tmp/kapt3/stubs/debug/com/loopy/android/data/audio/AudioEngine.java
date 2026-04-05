package com.loopy.android.data.audio;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0017\n\u0002\b\u0003\b\u0007\u0018\u0000 12\u00020\u0001:\u000212B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J1\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u001c2\n\b\u0002\u0010\u001d\u001a\u0004\u0018\u00010\u001cH\u0002\u00a2\u0006\u0002\u0010\u001eJ\u0006\u0010\u001f\u001a\u00020 J \u0010!\u001a\u00020 2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00192\b\b\u0002\u0010\"\u001a\u00020#J\u0006\u0010$\u001a\u00020 J4\u0010%\u001a\u00020 2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020(0'2\u0006\u0010)\u001a\u00020#2\u0006\u0010*\u001a\u00020#2\u000e\b\u0002\u0010+\u001a\b\u0012\u0004\u0012\u00020 0,J\u0006\u0010-\u001a\u00020 J\u0010\u0010.\u001a\u00020 2\u0006\u0010/\u001a\u000200H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R$\u0010\b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R$\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u0011@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016\u00a8\u00063"}, d2 = {"Lcom/loopy/android/data/audio/AudioEngine;", "", "()V", "audioTrack", "Landroid/media/AudioTrack;", "isInitialized", "", "value", "isMuted", "()Z", "setMuted", "(Z)V", "isPlaying", "playbackJob", "Lkotlinx/coroutines/Job;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "", "volume", "getVolume", "()F", "setVolume", "(F)V", "generatePianoSample", "note", "", "velocity", "timeInSeconds", "", "releaseTime", "(IIDLjava/lang/Double;)F", "initialize", "", "playNote", "durationMs", "", "release", "startPlayback", "events", "", "Lcom/loopy/android/data/audio/AudioEngine$PlaybackEvent;", "startTimeMicros", "loopDurationMicros", "onComplete", "Lkotlin/Function0;", "stopPlayback", "writeToAudioTrack", "samples", "", "Companion", "PlaybackEvent", "app_debug"})
public final class AudioEngine {
    private static final int SAMPLE_RATE = 44100;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "LoopyAudioEngine";
    @org.jetbrains.annotations.Nullable()
    private android.media.AudioTrack audioTrack;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job playbackJob;
    private boolean isPlaying = false;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    private float volume = 0.7F;
    private boolean isMuted = false;
    private boolean isInitialized = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.loopy.android.data.audio.AudioEngine.Companion Companion = null;
    
    @javax.inject.Inject()
    public AudioEngine() {
        super();
    }
    
    public final float getVolume() {
        return 0.0F;
    }
    
    public final void setVolume(float value) {
    }
    
    public final boolean isMuted() {
        return false;
    }
    
    public final void setMuted(boolean value) {
    }
    
    public final void initialize() {
    }
    
    public final void release() {
    }
    
    /**
     * Generate piano-like waveform using additive synthesis
     * Based on simplified harmonic series with envelope
     */
    private final float generatePianoSample(int note, int velocity, double timeInSeconds, java.lang.Double releaseTime) {
        return 0.0F;
    }
    
    /**
     * Play a note through the audio engine
     */
    public final void playNote(int note, int velocity, long durationMs) {
    }
    
    /**
     * Start continuous playback from an event queue
     * This is called during loop playback
     */
    public final void startPlayback(@org.jetbrains.annotations.NotNull()
    java.util.List<com.loopy.android.data.audio.AudioEngine.PlaybackEvent> events, long startTimeMicros, long loopDurationMicros, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onComplete) {
    }
    
    public final void stopPlayback() {
    }
    
    private final void writeToAudioTrack(short[] samples) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/loopy/android/data/audio/AudioEngine$Companion;", "", "()V", "SAMPLE_RATE", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * Represents an event for audio playback with timing information
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0017"}, d2 = {"Lcom/loopy/android/data/audio/AudioEngine$PlaybackEvent;", "", "note", "", "velocity", "timestampMicros", "", "(IIJ)V", "getNote", "()I", "getTimestampMicros", "()J", "getVelocity", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
    public static final class PlaybackEvent {
        private final int note = 0;
        private final int velocity = 0;
        private final long timestampMicros = 0L;
        
        public PlaybackEvent(int note, int velocity, long timestampMicros) {
            super();
        }
        
        public final int getNote() {
            return 0;
        }
        
        public final int getVelocity() {
            return 0;
        }
        
        public final long getTimestampMicros() {
            return 0L;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        public final long component3() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.loopy.android.data.audio.AudioEngine.PlaybackEvent copy(int note, int velocity, long timestampMicros) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
}