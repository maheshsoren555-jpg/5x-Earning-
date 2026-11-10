package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LiveStream
import com.example.model.StreamQuality
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LivePlayerView(
    stream: LiveStream,
    selectedQuality: StreamQuality,
    onQualitySelected: (StreamQuality) -> Unit,
    qualityOptions: List<StreamQuality>,
    reactions: List<String>,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var showStatsForGeeks by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(4000)
            showControls = false
        }
    }

    // Dynamic stream pulse transition
    val infiniteTransition = rememberInfiniteTransition(label = "player_hud")
    val hudScanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hud_scan"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isFullscreen) 16f / 10f else 16f / 9f)
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
            .testTag("live_player_container")
    ) {
        // Video Layer: either image preview or high tech stream animation canvas
        if (stream.previewDrawableRes != null) {
            Image(
                painter = painterResource(id = stream.previewDrawableRes),
                contentDescription = stream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // High-tech simulated live stream canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Dark cyber stream background gradient
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E2638), Color(0xFF090B12)),
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                        radius = canvasWidth * 0.7f
                    )
                )

                // Grid lines
                val gridSpacing = 40.dp.toPx()
                var x = 0f
                while (x < canvasWidth) {
                    drawLine(
                        color = Color(0xFF222C42).copy(alpha = 0.35f),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasHeight),
                        strokeWidth = 1f
                    )
                    x += gridSpacing
                }

                // Moving scan line
                val scanY = canvasHeight * hudScanLine
                drawLine(
                    color = StreamCyan.copy(alpha = 0.45f),
                    start = Offset(0f, scanY),
                    end = Offset(canvasWidth, scanY),
                    strokeWidth = 2f
                )
            }
        }

        // Live stream overlay elements (broadcaster watermark, viewer count, audio visualizer)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiveBadge()
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${stream.viewersCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AudioVisualizer(
                barCount = 8,
                height = 14.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Broadcaster PIP Camera Mock in bottom right corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 12.dp, end = 12.dp)
                .size(width = 72.dp, height = 48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2A344A), Color(0xFF141926))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = StreamCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "CAM 1",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(2.dp)
                )
            }
        }

        // Floating Reaction Emoji Animation in stream
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                reactions.takeLast(5).forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
        }

        // Stats for Geeks Overlay
        if (showStatsForGeeks) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "STATS FOR GEEKS",
                        color = StreamCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text("Resolution: ${selectedQuality.resolution} @ ${selectedQuality.fps}fps", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Bitrate: ${selectedQuality.bitrateKbps} kbps (CBR)", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Protocol: LL-HLS (CMAF)", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Latency to Broadcaster: ${stream.latencyMs} ms", color = StreamGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Buffer Health: 2.84s (Target: 3.0s)", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Edge Node: ${stream.cdnEdgeNode}", color = StreamGold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Dropped Frames: 0 / 14,280 (0.00%)", color = StreamGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // Interactive Player Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // Center Play/Pause button
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .testTag("player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bottom Controls Bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Mute button
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Volume",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Quality button with dropdown
                        Box {
                            TextButton(
                                onClick = { showQualityMenu = true },
                                modifier = Modifier.testTag("quality_selector_button"),
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedQuality.label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            DropdownMenu(
                                expanded = showQualityMenu,
                                onDismissRequest = { showQualityMenu = false },
                                modifier = Modifier.background(StreamSurfaceCard)
                            ) {
                                qualityOptions.forEach { quality ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    quality.label,
                                                    color = if (quality == selectedQuality) StreamCyan else StreamTextPrimary,
                                                    fontWeight = if (quality == selectedQuality) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text("${quality.bitrateKbps}k", color = StreamTextMuted, fontSize = 12.sp)
                                            }
                                        },
                                        onClick = {
                                            onQualitySelected(quality)
                                            showQualityMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Stats for geeks toggle
                        IconButton(
                            onClick = { showStatsForGeeks = !showStatsForGeeks },
                            modifier = Modifier.size(36.dp).testTag("stats_for_geeks_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Stats for Geeks",
                                tint = if (showStatsForGeeks) StreamCyan else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Fullscreen button
                        IconButton(
                            onClick = { isFullscreen = !isFullscreen },
                            modifier = Modifier.size(36.dp).testTag("fullscreen_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
