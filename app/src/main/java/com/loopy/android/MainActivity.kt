package com.loopy.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.loopy.android.ui.screens.LooperScreen
import com.loopy.android.ui.theme.LoopyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
        permissions.entries.forEach { entry ->
            when (entry.key) {
                Manifest.permission.RECORD_AUDIO -> {
                    if (!entry.value) {
                        // Audio recording permission denied
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check for MIDI support
        checkMidiSupport()
        
        // Request permissions
        requestPermissions()
        
        setContent {
            LoopyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LooperScreen()
                }
            }
        }
    }

    private fun checkMidiSupport() {
        val midiMgr = getSystemService(Context.MIDI_SERVICE) as? MidiManager
        if (midiMgr != null) {
            val devices = midiMgr.devices
            if (devices.isEmpty()) {
                // No MIDI devices - user will need to connect one
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        
        // Record audio permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        
        // Storage permissions for export
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses MediaStore, no permission needed for Downloads
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            // No special permission needed
        } else {
            // Android 10-12
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Note: Android 10+ apps can't request external storage permission
                // We'll use MediaStore instead
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}