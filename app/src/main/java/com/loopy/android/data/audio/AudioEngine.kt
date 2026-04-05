package com.loopy.android.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.math.max
import kotlin.math.tanh

@Singleton
class AudioEngine @Inject constructor() {
    
    companion object {
        private const val SAMPLE_RATE = 44100
        private const val TAG = "LoopyAudioEngine"
    }
    
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private var isPlaying = false
    
    // Waveform generation parameters
    private val scope = CoroutineScope(Dispatchers.Default)
    
    // Volume (0.0 - 1.0)
    var volume: Float = 0.7f
        set(value) {
            field = value.coerceIn(0f, 1f)
            audioTrack?.setVolume(field)
        }
    
    // Mute state
    var isMuted: Boolean = false
        set(value) {
            field = value
            if (isInitialized) {
                audioTrack?.setVolume(if (field) 0f else volume)
            }
        }
    
    private var isInitialized = false

    fun initialize() {
        if (isInitialized) return
        
        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ) * 2
            
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            
            audioTrack?.setVolume(if (isMuted) 0f else volume)
            audioTrack?.play()
            isInitialized = true
            Log.d(TAG, "AudioEngine initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing AudioEngine", e)
        }
    }

    fun release() {
        isPlaying = false
        playbackJob?.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        isInitialized = false
        Log.d(TAG, "AudioEngine released")
    }

    /**
     * Generate piano-like waveform using additive synthesis
     * Based on simplified harmonic series with envelope
     */
    private fun generatePianoSample(
        note: Int,
        velocity: Int,
        timeInSeconds: Double,
        releaseTime: Double? = null
    ): Float {
        // Convert MIDI note to frequency
        val frequency = 440.0 * 2.0.pow((note - 69) / 12.0)
        
        // Normalized velocity (0-1)
        val vel = (velocity / 127.0).coerceIn(0.0, 1.0)
        
        // Refined ADSR envelope for Piano
        val attackTime = 0.003
        val decayTime = 0.2
        val sustainLevel = 0.4
        val releaseTimeSec = releaseTime ?: 0.4
        
        val envelope = when {
            releaseTime != null -> {
                // Release phase
                val releaseProgress = timeInSeconds / releaseTimeSec
                if (releaseProgress >= 1.0) 0f else (sustainLevel * (1 - releaseProgress)).toFloat()
            }
            timeInSeconds < attackTime -> {
                // Attack phase
                (timeInSeconds / attackTime * vel).toFloat()
            }
            timeInSeconds < attackTime + decayTime -> {
                // Decay phase
                val decayProgress = (timeInSeconds - attackTime) / decayTime
                (vel - (vel - sustainLevel) * decayProgress).toFloat()
            }
            else -> {
                // Natural decay even during sustain
                val sustainDecay = max(0.0, sustainLevel - (timeInSeconds - attackTime - decayTime) * 0.05)
                (sustainDecay * vel).toFloat()
            }
        }
        
        if (envelope <= 0f) return 0f
        
        var sample = 0.0
        
        // Rich harmonic content with frequency-dependent decay (higher harmonics decay faster)
        val harmonics = intArrayOf(1, 2, 3, 4, 5, 6, 8)
        val amplitudes = doubleArrayOf(1.0, 0.7, 0.4, 0.25, 0.1, 0.05, 0.02)
        val inharmonicity = 0.0001 // Slight stretching of harmonics typical in acoustic pianos
        
        for (i in harmonics.indices) {
            val h = harmonics[i]
            val amp = amplitudes[i]
            // Inharmonicity formula: f_n = n * f_1 * sqrt(1 + B * n^2)
            val stretchedFreq = h * frequency * sqrt(1.0 + inharmonicity * h * h)
            
            // Higher frequencies decay much faster
            val harmonicDecay = exp(-timeInSeconds * (h * 0.8))
            
            sample += amp * harmonicDecay * sin(2 * PI * stretchedFreq * timeInSeconds)
        }
        
        // Add chorus effect from natural multi-string detuning per key
        val detuneFast = frequency * 1.002
        val detuneSlow = frequency * 0.998
        sample += 0.4 * exp(-timeInSeconds * 1.0) * sin(2 * PI * detuneFast * timeInSeconds)
        sample += 0.4 * exp(-timeInSeconds * 1.0) * sin(2 * PI * detuneSlow * timeInSeconds)
        
        // Add subtle hammer strike
        val hammerPhase = exp(-timeInSeconds * 40.0)
        sample += 0.1 * hammerPhase * sin(2 * PI * (frequency * 0.5) * timeInSeconds)
        
        // Normalize 
        sample *= 0.4 * envelope
        
        // Saturation/compression for warmth
        sample = tanh(sample * 1.5)
        
        return sample.toFloat().coerceIn(-1f, 1f)
    }

    /**
     * Play a note through the audio engine
     */
    fun playNote(note: Int, velocity: Int, durationMs: Long = 1000) {
        if (!isInitialized || isMuted) return
        
        scope.launch {
            val samples = mutableListOf<Short>()
            val numSamples = (SAMPLE_RATE * durationMs / 1000).toInt()
            
            for (i in 0 until numSamples) {
                val timeInSeconds = i.toDouble() / SAMPLE_RATE
                val sample = generatePianoSample(note, velocity, timeInSeconds)
                samples.add((sample * Short.MAX_VALUE).toInt().toShort())
            }
            
            writeToAudioTrack(samples.toShortArray())
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

    /**
     * Start continuous playback from an event queue
     * This is called during loop playback
     */
    fun startPlayback(
        events: List<PlaybackEvent>,
        startTimeMicros: Long,
        loopDurationMicros: Long,
        onComplete: () -> Unit = {}
    ) {
        if (!isInitialized) return
        
        stopPlayback()
        isPlaying = true
        
        playbackJob = scope.launch {
            val bufferSize = 4096
            val buffer = ShortArray(bufferSize)
            
            val startTimeNanos = System.nanoTime()
            var sampleIndex = 0L
            var loopCount = 0
            
            while (isActive && isPlaying) {
                // Calculate current position in the loop
                val elapsedNanos = System.nanoTime() - startTimeNanos
                val elapsedMicros = elapsedNanos / 1000
                val loopPosition = (elapsedMicros % loopDurationMicros)
                
                // Find active notes at this position
                val activeNotes = mutableSetOf<Int>()
                val noteEvents = mutableListOf<PlaybackEvent>()
                
                // Process all events - find those near the current loop position
                for (event in events) {
                    if (event.timestampMicros >= loopPosition - 50000 && event.timestampMicros <= loopPosition + 50000) {
                        if (event.velocity > 0) {
                            activeNotes.add(event.note)
                            noteEvents.add(event)
                        } else {
                            activeNotes.remove(event.note)
                        }
                    }
                }
                
                // Generate samples for this buffer
                for (i in 0 until bufferSize) {
                    val currentTimeSeconds = (sampleIndex + i).toDouble() / SAMPLE_RATE
                    var sample = 0f
                    
                    for (event in noteEvents) {
                        sample += generatePianoSample(event.note, event.velocity, currentTimeSeconds)
                    }
                    
                    buffer[i] = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE * 0.3).toInt().toShort()
                }
                
                writeToAudioTrack(buffer)
                sampleIndex += bufferSize
                
                // Check if we've completed a full loop
                if (elapsedMicros >= loopDurationMicros * (loopCount + 1)) {
                    loopCount++
                }
            }
            
            onComplete()
        }
    }

    fun stopPlayback() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
    }

    private fun writeToAudioTrack(samples: ShortArray) {
        if (!isInitialized) return
        try {
            audioTrack?.write(samples, 0, samples.size)
        } catch (e: Exception) {
            Log.e(TAG, "Error writing audio", e)
        }
    }
}