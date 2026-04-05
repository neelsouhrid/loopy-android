package com.loopy.android.domain.model;

import java.util.UUID;

/**
 * Represents a single MIDI event (note on/off, control change, etc.)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001c\u001a\u00020\nH\u00c6\u0003JP\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001\u00a2\u0006\u0002\u0010\u001eJ\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00d6\u0001J\t\u0010#\u001a\u00020$H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0015\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0015\u0010\b\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0015\u0010\u000fR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u0016\u0010\u000f\u00a8\u0006%"}, d2 = {"Lcom/loopy/android/domain/model/MidiEvent;", "", "type", "Lcom/loopy/android/domain/model/MidiEventType;", "channel", "", "note", "velocity", "value", "timestampMicros", "", "(Lcom/loopy/android/domain/model/MidiEventType;ILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;J)V", "getChannel", "()I", "getNote", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getTimestampMicros", "()J", "getType", "()Lcom/loopy/android/domain/model/MidiEventType;", "getValue", "getVelocity", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Lcom/loopy/android/domain/model/MidiEventType;ILjava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;J)Lcom/loopy/android/domain/model/MidiEvent;", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class MidiEvent {
    @org.jetbrains.annotations.NotNull()
    private final com.loopy.android.domain.model.MidiEventType type = null;
    private final int channel = 0;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer note = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer velocity = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer value = null;
    private final long timestampMicros = 0L;
    
    public MidiEvent(@org.jetbrains.annotations.NotNull()
    com.loopy.android.domain.model.MidiEventType type, int channel, @org.jetbrains.annotations.Nullable()
    java.lang.Integer note, @org.jetbrains.annotations.Nullable()
    java.lang.Integer velocity, @org.jetbrains.annotations.Nullable()
    java.lang.Integer value, long timestampMicros) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.loopy.android.domain.model.MidiEventType getType() {
        return null;
    }
    
    public final int getChannel() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getNote() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getVelocity() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getValue() {
        return null;
    }
    
    public final long getTimestampMicros() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.loopy.android.domain.model.MidiEventType component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component5() {
        return null;
    }
    
    public final long component6() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.loopy.android.domain.model.MidiEvent copy(@org.jetbrains.annotations.NotNull()
    com.loopy.android.domain.model.MidiEventType type, int channel, @org.jetbrains.annotations.Nullable()
    java.lang.Integer note, @org.jetbrains.annotations.Nullable()
    java.lang.Integer velocity, @org.jetbrains.annotations.Nullable()
    java.lang.Integer value, long timestampMicros) {
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