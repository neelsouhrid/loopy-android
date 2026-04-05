package com.loopy.android.data.audio

import android.content.Context
import android.media.SoundPool
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "LoopyAudioEngine"
    }

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<Int, Int>()

    var volume: Float = 0.7f
    var isMuted: Boolean = false

    private var isInitialized = false

    private fun getNoteName(midiNote: Int): String {
        val octave = (midiNote / 12) - 1
        val notes = arrayOf("c", "db", "d", "eb", "e", "f", "gb", "g", "ab", "a", "bb", "b")
        val noteName = notes[midiNote % 12]
        return "$noteName$octave"
    }

    fun initialize() {
        if (isInitialized) return
        
        try {
            soundPool = SoundPool.Builder()
                .setMaxStreams(32)
                .build()
                
            // Load downloaded piano samples
            for (note in 12..108) {
                val fileName = getNoteName(note)
                val resId = context.resources.getIdentifier(fileName, "raw", context.packageName)
                if (resId != 0) {
                    val soundId = soundPool?.load(context, resId, 1)
                    if (soundId != null) {
                        soundMap[note] = soundId
                    }
                }
            }
            
            isInitialized = true
            Log.d(TAG, "AudioEngine initialized with SoundPool")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing AudioEngine", e)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundMap.clear()
        isInitialized = false
        Log.d(TAG, "AudioEngine released")
    }

    fun playNote(note: Int, velocity: Int) {
        if (!isInitialized || isMuted) return
        val soundId = soundMap[note]
        if (soundId != null && soundId != 0) {
            // Velocity mapped 0f to 1.0f
            val vol = (velocity / 127f) * volume
            soundPool?.play(soundId, vol, vol, 1, 0, 1f)
        }
    }

    /**
     * Represents an event for audio playback with timing information
     */
    data class PlaybackEvent(
        val note: Int,
        val velocity: Int,
        val timestampMicros: Long
    )

    // Maintaining original stubs so compile doesn't fail immediately in Repository
    fun startPlayback(
        events: List<PlaybackEvent>,
        startTimeMicros: Long,
        loopDurationMicros: Long,
        onComplete: () -> Unit = {}
    ) {
        // Continuous generation replaced by discrete SoundPool calls in LooperRepository
    }

    fun stopPlayback() {
        // Continuous queue replaced
    }
}