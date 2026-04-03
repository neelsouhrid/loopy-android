package com.loopy.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loopy.android.domain.model.Track
import com.loopy.android.ui.theme.TrackEmpty
import com.loopy.android.ui.theme.TrackPlaying
import com.loopy.android.ui.theme.TrackRecorded
import com.loopy.android.ui.theme.TrackRecording
import com.loopy.android.ui.theme.TrackSelected

@Composable
fun TrackButton(
    track: Track,
    isSelected: Boolean,
    isPlaying: Boolean,
    isRecording: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isRecording -> TrackRecording
            isPlaying -> TrackPlaying
            track.isNotEmpty && isSelected -> TrackSelected
            track.isNotEmpty && !isSelected -> TrackRecorded
            isSelected -> TrackSelected.copy(alpha = 0.6f)
            else -> TrackEmpty
        },
        label = "trackColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isRecording) 1.05f else 1f,
        label = "trackScale"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .aspectRatio(1.5f)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Track ${track.id + 1}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected || isPlaying || isRecording) FontWeight.Bold else FontWeight.Medium
            )
            
            Text(
                text = when {
                    isRecording -> "REC"
                    isPlaying -> "PLAY"
                    track.isNotEmpty -> "${(track.durationMicros / 1_000_000.0).toFloat()}s"
                    else -> "Empty"
                },
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TrackGrid(
    tracks: List<Track>,
    selectedIndex: Int,
    playingIndex: Int?,
    recordingIndex: Int?,
    onTrackClick: (Int) -> Unit,
    onTrackLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: Tracks 0-4
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..4) {
                TrackButton(
                    track = tracks[i],
                    isSelected = selectedIndex == i,
                    isPlaying = playingIndex == i,
                    isRecording = recordingIndex == i,
                    onClick = { onTrackClick(i) },
                    onLongClick = { onTrackLongClick(i) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Row 2: Tracks 5-9
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            for (i in 5..9) {
                TrackButton(
                    track = tracks[i],
                    isSelected = selectedIndex == i,
                    isPlaying = playingIndex == i,
                    isRecording = recordingIndex == i,
                    onClick = { onTrackClick(i) },
                    onLongClick = { onTrackLongClick(i) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}