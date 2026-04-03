package com.loopy.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loopy.android.data.repository.LooperRepository
import com.loopy.android.domain.model.LooperMode
import com.loopy.android.domain.model.PlaybackState
import com.loopy.android.domain.model.Session
import com.loopy.android.ui.components.TrackGrid
import com.loopy.android.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LooperViewModel @Inject constructor(
    private val looperRepository: LooperRepository
) : ViewModel() {

    val currentSession: StateFlow<Session?> = looperRepository.currentSession
    val selectedTrackIndex: StateFlow<Int> = looperRepository.selectedTrackIndex
    val mode: StateFlow<LooperMode> = looperRepository.mode
    val playbackState: StateFlow<PlaybackState> = looperRepository.playbackState
    val isMuted: StateFlow<Boolean> = looperRepository.isMuted
    val sessions: StateFlow<List<Session>> = looperRepository.sessions

    val isMidiConnected: StateFlow<Boolean> = looperRepository.let { repo ->
        // This would need to be exposed from the repository
        MutableStateFlow(false)
    }

    init {
        viewModelScope.launch {
            looperRepository.initialize()
        }
    }

    fun selectTrack(index: Int) = looperRepository.selectTrack(index)
    fun toggleMode() = looperRepository.toggleMode()
    fun startStop() = looperRepository.startStop()
    fun clearTrack(index: Int) = looperRepository.clearTrack(index)
    fun clearAllTracks() = looperRepository.clearAllTracks()
    fun toggleMute() = looperRepository.toggleMute()
    fun createSession(name: String) = looperRepository.createSession(name)
    fun switchSession(id: String) = looperRepository.switchSession(id)
    fun renameSession(id: String, name: String) = looperRepository.renameSession(id, name)
    fun deleteSession(id: String) = looperRepository.deleteSession(id)

    fun exportMidi(merged: Boolean) = viewModelScope.launch {
        looperRepository.exportMidi(merged)
    }

    fun exportAudio() = viewModelScope.launch {
        looperRepository.exportAudio()
    }

    override fun onCleared() {
        super.onCleared()
        looperRepository.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LooperScreen(
    viewModel: LooperViewModel = hiltViewModel()
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsState()
    val mode by viewModel.mode.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showSessionsSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showNewSessionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = currentSession?.name ?: "Loopy",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showSessionsSheet = true }) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = "Sessions",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Mute button
                    IconButton(onClick = { viewModel.toggleMute() }) {
                        Icon(
                            if (isMuted) Icons.Outlined.VolumeOff else Icons.Outlined.VolumeUp,
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                            tint = if (isMuted) Color.Gray else Color.White
                        )
                    }
                    // Menu button
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export MIDI (Merged)") },
                            onClick = {
                                showMenu = false
                                viewModel.exportMidi(merged = true)
                            },
                            leadingIcon = { Icon(Icons.Outlined.Audiotrack, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Export MIDI (Separate)") },
                            onClick = {
                                showMenu = false
                                viewModel.exportMidi(merged = false)
                            },
                            leadingIcon = { Icon(Icons.Outlined.Audiotrack, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Audio (MP3)") },
                            onClick = {
                                showMenu = false
                                viewModel.exportAudio()
                            },
                            leadingIcon = { Icon(Icons.Outlined.MusicNote, null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Clear All Tracks") },
                            onClick = {
                                showMenu = false
                                showClearConfirmDialog = true
                            },
                            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = ErrorRed) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ModeChip(
                    text = "RECORD",
                    isSelected = mode == LooperMode.RECORD,
                    color = TrackRecording,
                    onClick = { viewModel.toggleMode() },
                    enabled = playbackState == PlaybackState.STOPPED
                )
                Spacer(modifier = Modifier.width(16.dp))
                ModeChip(
                    text = "PLAY",
                    isSelected = mode == LooperMode.PLAY,
                    color = TrackPlaying,
                    onClick = { viewModel.toggleMode() },
                    enabled = playbackState == PlaybackState.STOPPED
                )
            }

            // Track grid
            TrackGrid(
                tracks = currentSession?.tracks ?: emptyList(),
                selectedIndex = selectedTrackIndex,
                playingIndex = if (playbackState == PlaybackState.PLAYING) selectedTrackIndex else null,
                recordingIndex = if (playbackState == PlaybackState.RECORDING) selectedTrackIndex else null,
                onTrackClick = { viewModel.selectTrack(it) },
                onTrackLongClick = { 
                    viewModel.selectTrack(it)
                    showClearConfirmDialog = true
                },
                modifier = Modifier.weight(1f)
            )

            // Transport controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left - Clear track
                IconButton(
                    onClick = { viewModel.clearTrack(selectedTrackIndex) },
                    enabled = playbackState == PlaybackState.STOPPED
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Clear Track",
                        tint = if (playbackState == PlaybackState.STOPPED) Color.White else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Center - Play/Stop button
                FloatingActionButton(
                    onClick = { viewModel.startStop() },
                    containerColor = if (playbackState == PlaybackState.STOPPED) {
                        if (mode == LooperMode.RECORD) TrackRecording else TrackPlaying
                    } else {
                        SurfaceDark
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        when (playbackState) {
                            PlaybackState.STOPPED -> if (mode == LooperMode.RECORD) Icons.Default.FiberManualRecord else Icons.Default.PlayArrow
                            else -> Icons.Default.Stop
                        },
                        contentDescription = "Play/Stop",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Right - Mode toggle
                IconButton(
                    onClick = { viewModel.toggleMode() },
                    enabled = playbackState == PlaybackState.STOPPED
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = "Toggle Mode",
                        tint = if (playbackState == PlaybackState.STOPPED) Color.White else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Status bar
            Text(
                text = when (playbackState) {
                    PlaybackState.STOPPED -> "Ready"
                    PlaybackState.PLAYING -> "Playing..."
                    PlaybackState.RECORDING -> "Recording..."
                },
                color = when (playbackState) {
                    PlaybackState.RECORDING -> TrackRecording
                    PlaybackState.PLAYING -> TrackPlaying
                    else -> Color.Gray
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    // Clear confirmation dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Track?") },
            text = { Text("Are you sure you want to clear this track? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearTrack(selectedTrackIndex)
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Sessions bottom sheet
    if (showSessionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSessionsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sessions", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = { showNewSessionDialog = true }) {
                        Icon(Icons.Default.Add, null)
                        Text("New")
                    }
                }
                
                sessions.forEach { session ->
                    ListItem(
                        headlineContent = { Text(session.name) },
                        supportingContent = { Text("${session.tracks.count { it.isNotEmpty }} tracks") },
                        leadingContent = {
                            RadioButton(
                                selected = session.id == currentSession?.id,
                                onClick = { viewModel.switchSession(session.id) }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // New session dialog
    if (showNewSessionDialog) {
        var sessionName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewSessionDialog = false },
            title = { Text("New Session") },
            text = {
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (sessionName.isNotBlank()) {
                            viewModel.createSession(sessionName)
                            showNewSessionDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewSessionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ModeChip(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        color = if (isSelected) color else SurfaceVariantDark,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}