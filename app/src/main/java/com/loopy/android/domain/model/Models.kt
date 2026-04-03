package com.loopy.android.domain.model

import java.util.UUID

/**
 * Represents a single MIDI event (note on/off, control change, etc.)
 */
data class MidiEvent(
    val type: MidiEventType,
    val channel: Int,
    val note: Int? = null,
    val velocity: Int? = null,
    val value: Int? = null,
    val timestampMicros: Long
)

enum class MidiEventType {
    NOTE_ON,
    NOTE_OFF,
    CONTROL_CHANGE,
    PROGRAM_CHANGE,
    PITCH_BEND
}

/**
 * Represents a single track containing recorded MIDI events
 */
data class Track(
    val id: Int, // Track number 0-9
    val events: List<MidiEvent> = emptyList(),
    val programChange: Int? = null, // MIDI program change for tone
    val durationMicros: Long = 0L
) {
    val isEmpty: Boolean get() = events.isEmpty()
    val isNotEmpty: Boolean get() = events.isNotEmpty()
}

/**
 * Represents a session containing multiple tracks
 */
data class Session(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val tracks: List<Track> = List(10) { Track(id = it) },
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val longestTrackDuration: Long
        get() = tracks.filter { it.isNotEmpty }.maxOfOrNull { it.durationMicros } ?: 0L
}

/**
 * Playback state
 */
enum class PlaybackState {
    STOPPED,
    PLAYING,
    RECORDING
}

/**
 * Current mode (Record vs Play)
 */
enum class LooperMode {
    RECORD,
    PLAY
}