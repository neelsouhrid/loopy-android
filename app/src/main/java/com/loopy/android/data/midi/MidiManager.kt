package com.loopy.android.data.midi

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiReceiver
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MidiManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager
    private var midiDevice: MidiDevice? = null
    private var midiReceiver: MidiReceiver? = null
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()
    
    private val _availableDevices = MutableStateFlow<List<MidiDeviceInfo>>(emptyList())
    val availableDevices: StateFlow<List<MidiDeviceInfo>> = _availableDevices.asStateFlow()
    
    // Callback for incoming MIDI messages
    var onMidiMessage: ((ByteArray, Int, Int) -> Unit)? = null

    init {
        refreshDevices()
    }

    fun refreshDevices() {
        try {
            val deviceInfos = midiManager.getDeviceInfos()
            _availableDevices.value = deviceInfos.filter { it.outputPortCount > 0 }
            Log.d(TAG, "Found ${deviceInfos.size} MIDI devices")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MIDI devices", e)
        }
    }

    fun openDevice(deviceInfo: MidiDeviceInfo): Boolean {
        return try {
            midiManager.openDevice(deviceInfo) { device ->
                if (device != null) {
                    midiDevice = device
                    midiReceiver = device.openOutputPort(0)
                    _isConnected.value = true
                    _connectedDeviceName.value = deviceInfo.name
                    Log.d(TAG, "Connected to MIDI device: ${deviceInfo.name}")
                    
                    // Set up receiver for incoming messages
                    device.openInputPort(0)?.let { inputPort ->
                        inputPort.connect(receiveReceiver)
                    }
                } else {
                    _isConnected.value = false
                    _connectedDeviceName.value = null
                    Log.e(TAG, "Failed to open MIDI device")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error opening MIDI device", e)
            false
        }
    }

    fun closeDevice() {
        try {
            midiReceiver?.close()
            midiDevice?.close()
            midiDevice = null
            midiReceiver = null
            _isConnected.value = false
            _connectedDeviceName.value = null
            Log.d(TAG, "MIDI device disconnected")
        } catch (e: Exception) {
            Log.e(TAG, "Error closing MIDI device", e)
        }
    }

    /**
     * Send MIDI message to the connected device
     * @param data MIDI message bytes
     * @param offset offset in the data array
     * @param count number of bytes to send
     */
    fun sendMidi(data: ByteArray, offset: Int = 0, count: Int = data.size) {
        if (_isConnected.value && midiReceiver != null) {
            try {
                midiReceiver?.send(data, offset, count)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending MIDI", e)
            }
        }
    }

    /**
     * Send Note On message
     */
    fun noteOn(channel: Int, note: Int, velocity: Int) {
        val status = (0x90 or channel).toByte()
        sendMidi(byteArrayOf(status, note.toByte(), velocity.toByte()))
    }

    /**
     * Send Note Off message
     */
    fun noteOff(channel: Int, note: Int, velocity: Int = 0) {
        val status = (0x80 or channel).toByte()
        sendMidi(byteArrayOf(status, note.toByte(), velocity.toByte()))
    }

    /**
     * Send Program Change
     */
    fun programChange(channel: Int, program: Int) {
        val status = (0xC0 or channel).toByte()
        sendMidi(byteArrayOf(status, program.toByte()))
    }

    /**
     * Send Control Change
     */
    fun controlChange(channel: Int, controller: Int, value: Int) {
        val status = (0xB0 or channel).toByte()
        sendMidi(byteArrayOf(status, controller.toByte(), value.toByte()))
    }

    /**
     * Send All Notes Off (MIDI Panic)
     */
    fun allNotesOff() {
        for (channel in 0..15) {
            controlChange(channel, 123, 0) // All Notes Off
            controlChange(channel, 121, 0) // Reset All Controllers
        }
    }

    private val receiveReceiver = object : MidiReceiver() {
        override fun onSend(msg: ByteArray?, offset: Int, count: Int, timestamp: Long) {
            msg?.let {
                onMidiMessage?.invoke(it, offset, count)
            }
        }
    }

    companion object {
        private const val TAG = "LoopyMidiManager"
    }
}