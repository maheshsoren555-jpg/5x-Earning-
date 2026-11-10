package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.components.AudioVisualizer
import com.example.ui.components.LiveBadge
import com.example.ui.theme.*
import com.example.viewmodel.StreamUiState
import com.example.viewmodel.StreamViewModel

@Composable
fun BroadcastStudioScreen(
    viewModel: StreamViewModel,
    uiState: StreamUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val studio = uiState.broadcastStudioState

    var hasCameraPermission by remember { mutableStateOf(false) }
    var hasAudioPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StreamBgDark)
    ) {
        // Camera Broadcast Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .background(Color.Black)
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                                val cameraSelector = if (studio.isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        // Re-bind when camera facing changes
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(view.context)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = view.surfaceProvider
                                }
                                val cameraSelector = if (studio.isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                                camera.cameraControl.enableTorch(studio.isTorchOn && !studio.isFrontCamera)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(view.context))
                    }
                )
            } else {
                // Permission placeholder
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = null,
                        tint = StreamTextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Camera & Audio Access Needed",
                        color = StreamTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Grant permissions to stream live from your device camera and microphone.",
                        color = StreamTextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StreamCyan),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Grant Permissions", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Top Status Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (studio.isBroadcasting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiveBadge(text = "ON AIR")
                        val minutes = studio.broadcastSeconds / 60
                        val seconds = studio.broadcastSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Text("${studio.activeViewers}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("OFFLINE (STUDIO READY)", color = StreamTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Ingest Health Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (studio.isBroadcasting) StreamGreen else StreamTextMuted)
                    )
                    Text(
                        text = if (studio.isBroadcasting) "${studio.currentIngestBitrate} kbps" else "0 kbps",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Camera Controls Bar (Flip, Torch, Mic)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingActionButton(
                    onClick = { viewModel.toggleCameraFacing() },
                    containerColor = StreamSurfaceCard,
                    contentColor = StreamCyan,
                    modifier = Modifier.size(44.dp).testTag("flip_camera_button")
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Flip Camera", modifier = Modifier.size(20.dp))
                }

                FloatingActionButton(
                    onClick = { viewModel.toggleTorch() },
                    containerColor = if (studio.isTorchOn) StreamGold else StreamSurfaceCard,
                    contentColor = if (studio.isTorchOn) Color.Black else StreamTextPrimary,
                    modifier = Modifier.size(44.dp).testTag("torch_toggle_button")
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash", modifier = Modifier.size(20.dp))
                }

                FloatingActionButton(
                    onClick = { viewModel.toggleMic() },
                    containerColor = if (studio.isMicMuted) StreamLiveRed else StreamSurfaceCard,
                    contentColor = Color.White,
                    modifier = Modifier.size(44.dp).testTag("mic_toggle_button")
                ) {
                    Icon(
                        imageVector = if (studio.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mic",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Broadcaster Studio Deck & Stream Setup
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(StreamSurfaceDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Go Live Action Button
            Button(
                onClick = { viewModel.toggleBroadcast() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("go_live_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (studio.isBroadcasting) StreamLiveRed else StreamCyan
                ),
                shape = RoundedCornerShape(26.dp)
            ) {
                Icon(
                    imageVector = if (studio.isBroadcasting) Icons.Default.StopCircle else Icons.Default.Sensors,
                    contentDescription = null,
                    tint = if (studio.isBroadcasting) Color.White else Color.Black,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (studio.isBroadcasting) "END BROADCAST" else "START LIVE BROADCAST",
                    color = if (studio.isBroadcasting) Color.White else Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }

            // Stream Ingest Credentials (RTMP / SRT)
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "STUDIO INGEST CREDENTIALS",
                        color = StreamCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Ingest Server URL", color = StreamTextSecondary, fontSize = 11.sp)
                            Text(studio.ingestServerUrl, color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Ingest URL", studio.ingestServerUrl))
                                Toast.makeText(context, "Ingest URL copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = StreamCyan, modifier = Modifier.size(16.dp))
                        }
                    }

                    HorizontalDivider(color = Color(0xFF2E384D))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Stream Key (OBS / Wirecast / Mobile)", color = StreamTextSecondary, fontSize = 11.sp)
                            Text("••••••••••••••••••••", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Stream Key", studio.streamKey))
                                Toast.makeText(context, "Stream key copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = "Copy Key", tint = StreamGold, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Telemetry Gauges during broadcast
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StreamSurfaceCard)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Ingest FPS", color = StreamTextSecondary, fontSize = 10.sp)
                        Text("${studio.currentFps} fps", color = StreamGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StreamSurfaceCard)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Dropped Frames", color = StreamTextSecondary, fontSize = 10.sp)
                        Text("${studio.droppedFramesCount} (0.0%)", color = StreamGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StreamSurfaceCard)
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Mic Audio VU", color = StreamTextSecondary, fontSize = 10.sp)
                        AudioVisualizer(barCount = 6, height = 14.dp, isLive = studio.isBroadcasting && !studio.isMicMuted)
                    }
                }
            }
        }
    }
}
