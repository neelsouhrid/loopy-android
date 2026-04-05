package com.loopy.android.data.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.loopy.android.domain.model.Session
import com.loopy.android.domain.model.Track
import com.loopy.android.domain.model.MidiEvent
import com.loopy.android.domain.model.MidiEventType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class ExportManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LoopyExportManager"
    }

    /**
     * Export session to MIDI file(s)
     * @param session The session to export
     * @param merged If true, export all tracks to single file. If false, export each track separately
     * @return List of file paths where exports were saved
     */
    suspend fun exportMidi(session: Session, merged: Boolean = true): List<String> = withContext(Dispatchers.IO) {
        val exports = mutableListOf<String>()
        
        try {
            if (merged) {
                // Export merged file with all tracks
                val file = exportMergedMidi(session)
                if (file != null) exports.add(file)
            } else {
                // Export each track separately
                session.tracks.filter { it.isNotEmpty }.forEach { track ->
                    val file = exportTrackMidi(session.name, track, track.id + 1)
                    if (file != null) exports.add(file)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting MIDI", e)
        }
        
        exports
    }

    private fun exportMergedMidi(session: Session): String? {
        val fileName = "${sanitizeFileName(session.name)}_merged.mid"
        
        try {
            // Collect all events from all tracks
            val allEvents = mutableListOf<Quadruple<Long, Int, Int, Int>>() // timestamp, channel, note, velocity
            
            session.tracks.filter { it.isNotEmpty }.forEach { track ->
                track.events.forEach { event ->
                    when (event.type) {
                        MidiEventType.NOTE_ON, MidiEventType.NOTE_OFF -> {
                            if (event.note != null && event.velocity != null) {
                                // Convert timestamp from micros to ticks (480 ticks per beat, assume 120 BPM)
                                val ticks = (event.timestampMicros * 0.48).toLong()
                                allEvents.add(Quadruple(ticks, track.id, event.note, event.velocity))
                            }
                        }
                        else -> {}
                    }
                }
            }
            
            // Sort by timestamp
            allEvents.sortBy { it.first }
            
            // Write MIDI file
            val file = createExportFile(fileName)
            writeMidiFile(file, allEvents, session.longestTrackDuration)
            
            Log.d(TAG, "Exported merged MIDI: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting merged MIDI", e)
            return null
        }
    }

    private fun exportTrackMidi(sessionName: String, track: Track, trackNumber: Int): String? {
        val fileName = "${sanitizeFileName(sessionName)}_track${trackNumber}.mid"
        
        try {
            val events = track.events.mapNotNull { event ->
                when (event.type) {
                    MidiEventType.NOTE_ON, MidiEventType.NOTE_OFF -> {
                        if (event.note != null && event.velocity != null) {
                            val ticks = (event.timestampMicros * 0.48).toLong()
                            Quadruple(ticks, track.id, event.note, event.velocity)
                        } else null
                    }
                    else -> null
                }
            }.sortedBy { it.first }
            
            val file = createExportFile(fileName)
            writeMidiFile(file, events, track.durationMicros)
            
            Log.d(TAG, "Exported track $trackNumber MIDI: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting track MIDI", e)
            return null
        }
    }

    private fun writeMidiFile(file: File, events: List<Quadruple<Long, Int, Int, Int>>, durationMicros: Long) {
        val ticksPerQuarter = 480
        val microsecondsPerBeat = 500000L // 120 BPM
        
        // Calculate total ticks
        val totalTicks = ((durationMicros * 0.48) + 1000).toLong() // Add some padding
        
        FileOutputStream(file).use { fos ->
            // Write MIDI header
            // MThd
            fos.write(byteArrayOf(0x4D, 0x54, 0x68, 0x64)) // "MThd"
            fos.write(intToBytes(6)) // Header length
            fos.write(shortToBytes(0)) // Format 0
            fos.write(shortToBytes(1)) // 1 track
            fos.write(shortToBytes(ticksPerQuarter)) // Ticks per quarter
            
            // Write track
            // MTrk
            val trackData = ByteArrayOutputStream()
            
            // Tempo meta event
            trackData.write(0x00) // Delta time
            trackData.write(byteArrayOf(0xFF.toByte(), 0x51, 0x03)) // Tempo meta
            trackData.write(byteArrayOf(
                (microsecondsPerBeat shr 16).toByte(),
                (microsecondsPerBeat shr 8).toByte(),
                microsecondsPerBeat.toByte()
            ))
            
            // Write events
            var lastTick = 0L
            for ((tick, channel, note, velocity) in events) {
                val delta = (tick - lastTick).toInt()
                trackData.write(varLenToBytes(delta))
                
                val status = if (velocity > 0) 0x90 else 0x80
                trackData.write(byteArrayOf((status or channel).toByte(), note.toByte(), velocity.toByte()))
                
                lastTick = tick
            }
            
            // End of track
            trackData.write(0x00)
            trackData.write(byteArrayOf(0xFF.toByte(), 0x2F, 0x00))
            
            // Write track chunk
            fos.write(byteArrayOf(0x4D, 0x54, 0x72, 0x6B)) // "MTrk"
            fos.write(intToBytes(trackData.size()))
            fos.write(trackData.toByteArray())
        }
    }

    private fun intToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value shr 24).toByte(),
            (value shr 16).toByte(),
            (value shr 8).toByte(),
            value.toByte()
        )
    }

    private fun shortToBytes(value: Int): ByteArray {
        return byteArrayOf(
            (value shr 8).toByte(),
            value.toByte()
        )
    }

    private fun varLenToBytes(value: Int): ByteArray {
        val bytes = mutableListOf<Byte>()
        var v = value
        if (v == 0) {
            bytes.add(0)
        } else {
            while (v > 0) {
                bytes.add((v and 0x7F).toByte())
                v = v shr 7
            }
            bytes.reverse()
            // Set continuation bit
            for (i in 0 until bytes.size - 1) {
                bytes[i] = (bytes[i].toInt() or 0x80).toByte()
            }
        }
        return bytes.toByteArray()
    }

    private fun createExportFile(fileName: String): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(downloadsDir, fileName)
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(50)
    }

    // For MP3 export, we'd need to use MediaRecorder or a library like LAME
    // For now, this is a placeholder that would require additional native libraries
    suspend fun exportAudio(session: Session): String? = withContext(Dispatchers.IO) {
        // MP3 export requires native encoding
        // This is a placeholder - implementing full MP3 encoding would add significant complexity
        Log.w(TAG, "MP3 export not yet implemented")
        null
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}