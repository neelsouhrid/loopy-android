package com.loopy.android.data.datastore;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.loopy.android.domain.model.Session;
import com.loopy.android.domain.model.Track;
import com.loopy.android.domain.model.MidiEvent;
import com.loopy.android.domain.model.MidiEventType;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0016\u001a\u00020\rH\u0002J\u001c\u0010\u0017\u001a\u00020\u00182\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0016\u0010\u001a\u001a\u00020\r2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0016\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u0007H\u0086@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00182\u0006\u0010\u001f\u001a\u00020\rH\u0086@\u00a2\u0006\u0002\u0010 R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\tR\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\r0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00110\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\tR\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\r0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lcom/loopy/android/data/datastore/SessionDataStore;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "audioMuted", "Lkotlinx/coroutines/flow/Flow;", "", "getAudioMuted", "()Lkotlinx/coroutines/flow/Flow;", "audioMutedKey", "Landroidx/datastore/preferences/core/Preferences$Key;", "currentSessionId", "", "getCurrentSessionId", "currentSessionKey", "sessions", "", "Lcom/loopy/android/domain/model/Session;", "getSessions", "sessionsKey", "parseSessions", "json", "saveSessions", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "serializeSessions", "setAudioMuted", "muted", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setCurrentSession", "sessionId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SessionDataStore {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> sessionsKey = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> currentSessionKey = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> audioMutedKey = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.loopy.android.domain.model.Session>> sessions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.String> currentSessionId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.lang.Boolean> audioMuted = null;
    
    @javax.inject.Inject()
    public SessionDataStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
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
    public final kotlinx.coroutines.flow.Flow<java.lang.Boolean> getAudioMuted() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveSessions(@org.jetbrains.annotations.NotNull()
    java.util.List<com.loopy.android.domain.model.Session> sessions, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setCurrentSession(@org.jetbrains.annotations.NotNull()
    java.lang.String sessionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setAudioMuted(boolean muted, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.String serializeSessions(java.util.List<com.loopy.android.domain.model.Session> sessions) {
        return null;
    }
    
    private final java.util.List<com.loopy.android.domain.model.Session> parseSessions(java.lang.String json) {
        return null;
    }
}