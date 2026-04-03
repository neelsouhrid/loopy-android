package com.loopy.android.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.loopy.android.domain.model.Session
import com.loopy.android.domain.model.Track
import com.loopy.android.domain.model.MidiEvent
import com.loopy.android.domain.model.MidiEventType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "loopy_sessions")

@Singleton
class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sessionsKey = stringPreferencesKey("sessions")
    private val currentSessionKey = stringPreferencesKey("current_session_id")
    private val audioMutedKey = booleanPreferencesKey("audio_muted")

    val sessions: Flow<List<Session>> = context.dataStore.data.map { prefs ->
        val sessionsJson = prefs[sessionsKey] ?: "[]"
        parseSessions(sessionsJson)
    }

    val currentSessionId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[currentSessionKey]
    }

    val audioMuted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[audioMutedKey] ?: false
    }

    suspend fun saveSessions(sessions: List<Session>) {
        context.dataStore.edit { prefs ->
            prefs[sessionsKey] = serializeSessions(sessions)
        }
    }

    suspend fun setCurrentSession(sessionId: String) {
        context.dataStore.edit { prefs ->
            prefs[currentSessionKey] = sessionId
        }
    }

    suspend fun setAudioMuted(muted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[audioMutedKey] = muted
        }
    }

    private fun serializeSessions(sessions: List<Session>): String {
        val jsonArray = JSONArray()
        sessions.forEach { session ->
            val sessionJson = JSONObject().apply {
                put("id", session.id)
                put("name", session.name)
                put("createdAt", session.createdAt)
                put("updatedAt", session.updatedAt)
                put("tracks", JSONArray().apply {
                    session.tracks.forEach { track ->
                        put(JSONObject().apply {
                            put("id", track.id)
                            put("programChange", track.programChange)
                            put("durationMicros", track.durationMicros)
                            put("events", JSONArray().apply {
                                track.events.forEach { event ->
                                    put(JSONObject().apply {
                                        put("type", event.type.name)
                                        put("channel", event.channel)
                                        event.note?.let { put("note", it) }
                                        event.velocity?.let { put("velocity", it) }
                                        event.value?.let { put("value", it) }
                                        put("timestampMicros", event.timestampMicros)
                                    })
                                }
                            })
                        })
                    }
                })
            }
            jsonArray.put(sessionJson)
        }
        return jsonArray.toString()
    }

    private fun parseSessions(json: String): List<Session> {
        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).map { i ->
                val sessionJson = jsonArray.getJSONObject(i)
                val tracksJson = sessionJson.getJSONArray("tracks")
                val tracks = (0 until tracksJson.length()).map { j ->
                    val trackJson = tracksJson.getJSONObject(j)
                    val eventsJson = trackJson.getJSONArray("events")
                    val events = (0 until eventsJson.length()).map { k ->
                        val eventJson = eventsJson.getJSONObject(k)
                        MidiEvent(
                            type = MidiEventType.valueOf(eventJson.getString("type")),
                            channel = eventJson.getInt("channel"),
                            note = if (eventJson.has("note")) eventJson.getInt("note") else null,
                            velocity = if (eventJson.has("velocity")) eventJson.getInt("velocity") else null,
                            value = if (eventJson.has("value")) eventJson.getInt("value") else null,
                            timestampMicros = eventJson.getLong("timestampMicros")
                        )
                    }
                    Track(
                        id = trackJson.getInt("id"),
                        events = events,
                        programChange = if (trackJson.has("programChange")) trackJson.getInt("programChange") else null,
                        durationMicros = trackJson.getLong("durationMicros")
                    )
                }
                Session(
                    id = sessionJson.getString("id"),
                    name = sessionJson.getString("name"),
                    tracks = tracks,
                    createdAt = sessionJson.getLong("createdAt"),
                    updatedAt = sessionJson.getLong("updatedAt")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}