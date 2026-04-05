package com.loopy.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.loopy.android.domain.model.Track
import com.loopy.android.ui.theme.TrackEmpty
import com.loopy.android.ui.theme.TrackPlaying
import com.loopy.android.ui.theme.TrackRecorded
import com.loopy.android.ui.theme.TrackRecording
import com.loopy.android.ui.theme.TrackSelected

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackButton(
    track: Track,
    isSelected: Boolean,
    isPlaying: Boolean,
    isRecording: Boolean,
    playbackPosition: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onTwoFingerTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseTrackColor = track.color?.let { Color(it) } ?: TrackRecorded
    val backgroundColor by animateColorAsState(
        targetValue = when {
            track.isMuted -> Color.DarkGray
            isRecording -> TrackRecording
            isPlaying -> TrackPlaying
            track.isNotEmpty && isSelected -> TrackSelected
            track.isNotEmpty && !isSelected -> baseTrackColor
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
            .pointerInput(track.id) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        if (event.type == PointerEventType.Press) {
                            if (event.changes.count { it.pressed } >= 2) {
                                onTwoFingerTap()
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if ((track.isNotEmpty || isRecording || isPlaying) && playbackPosition > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = maxOf(size.width, size.height) * 1.5f
                drawArc(
                    color = Color.White.copy(alpha = 0.15f),
                    startAngle = -90f,
                    sweepAngle = 360f * playbackPosition,
                    useCenter = true,
                    topLeft = Offset((size.width - radius) / 2f, (size.height - radius) / 2f),
                    size = Size(radius, radius)
                )
            }
        }
        
        if (track.isMuted) {
            Icon(
                imageVector = Icons.Default.VolumeOff,
                contentDescription = "Muted",
                modifier = Modifier.align(Alignment.TopEnd),
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
        
        track.emoji?.let { emojiStr ->
            Text(
                text = emojiStr, 
                modifier = Modifier.align(Alignment.TopStart),
                fontSize = 16.sp
            )
        }
    
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = track.name ?: "Track ${track.id + 1}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (isSelected || isPlaying || isRecording) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Text(
                text = when {
                    isRecording -> "REC"
                    isPlaying -> "PLAY"
                    track.isNotEmpty -> "${String.format("%.1f", track.durationMicros / 1_000_000.0)}s"
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
    playbackPosition: Float,
    onTrackClick: (Int) -> Unit,
    onTrackLongClick: (Int) -> Unit,
    onTrackTwoFingerTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tracks) { i, track ->
            TrackButton(
                track = track,
                isSelected = selectedIndex == i,
                isPlaying = playingIndex == i,
                isRecording = recordingIndex == i,
                playbackPosition = playbackPosition,
                onClick = { onTrackClick(i) },
                onLongClick = { onTrackLongClick(i) },
                onTwoFingerTap = { onTrackTwoFingerTap(i) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}