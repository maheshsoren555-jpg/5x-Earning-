package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PipelineEngine
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.StreamUiState
import com.example.viewmodel.StreamViewModel

@Composable
fun PipelineConfigScreen(
    viewModel: StreamViewModel,
    uiState: StreamUiState,
    modifier: Modifier = Modifier
) {
    val config = uiState.pipelineConfig
    val telemetry = uiState.pipelineTelemetry
    val explanations = PipelineEngine.getArchitectureExplanation(config)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StreamBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_pipeline_hero_1794288481817),
                    contentDescription = "Video Pipeline Architecture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xDD090B10))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "VIDEO PIPELINE ARCHITECTURE",
                        color = StreamCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Ingest • Transcoding • CDN • Playback UI",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Telemetry Dashboard Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("pipeline_telemetry_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = StreamCyan)
                            Text(
                                text = "REAL-TIME PIPELINE TELEMETRY",
                                color = StreamTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (telemetry.isOptimal) StreamGreen else StreamLiveRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (telemetry.isOptimal) "OPTIMAL" else "HIGH LATENCY",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricBox(
                            title = "End-to-End Latency",
                            value = "${String.format("%.2f", telemetry.estimatedGlassToGlassLatencyMs / 1000f)}s",
                            highlight = telemetry.estimatedGlassToGlassLatencyMs < 2500,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "CDN Cache Hit",
                            value = "${String.format("%.1f", telemetry.cdnCacheHitRatioPct)}%",
                            highlight = telemetry.cdnCacheHitRatioPct > 95f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricBox(
                            title = "Ingress Bitrate",
                            value = "${String.format("%.1f", telemetry.ingressBandwidthMbps)} Mbps",
                            highlight = true,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Egress Bandwidth",
                            value = "${String.format("%.2f", telemetry.egressBandwidthGbps)} Gbps",
                            highlight = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricBox(
                            title = "Buffer Health",
                            value = "${String.format("%.1f", telemetry.bufferHealthSec)}s",
                            highlight = telemetry.bufferHealthSec >= 2.0f,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Edge PoP RTT",
                            value = "${telemetry.edgePoPResponseMs} ms",
                            highlight = telemetry.edgePoPResponseMs < 30,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Section 1: Ingest Configuration
        item {
            ConfigSection(
                title = "1. Ingest Pipeline (Streamer -> Cloud)",
                subtitle = "Protocol, codec, bitrate, and keyframe interval (GOP)",
                icon = Icons.Default.CloudUpload
            ) {
                Text("Ingest Protocol", color = StreamTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IngestProtocol.values().forEach { proto ->
                        val isSelected = config.ingestProtocol == proto
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updatePipelineConfig(config.copy(ingestProtocol = proto)) },
                            label = { Text(proto.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StreamCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Video Codec", color = StreamTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VideoCodec.values().forEach { codec ->
                        val isSelected = config.videoCodec == codec
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updatePipelineConfig(config.copy(videoCodec = codec)) },
                            label = { Text(codec.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StreamViolet,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Target Bitrate", color = StreamTextSecondary, fontSize = 12.sp)
                    Text("${config.targetIngestBitrateKbps} kbps", color = StreamCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = config.targetIngestBitrateKbps.toFloat(),
                    onValueChange = { viewModel.updatePipelineConfig(config.copy(targetIngestBitrateKbps = it.toInt())) },
                    valueRange = 2500f..12000f,
                    steps = 18,
                    colors = SliderDefaults.colors(thumbColor = StreamCyan, activeTrackColor = StreamCyan)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Keyframe Interval (GOP)", color = StreamTextSecondary, fontSize = 12.sp)
                    Text("${String.format("%.1f", config.keyframeIntervalSec)}s", color = StreamViolet, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = config.keyframeIntervalSec,
                    onValueChange = { viewModel.updatePipelineConfig(config.copy(keyframeIntervalSec = it)) },
                    valueRange = 0.5f..4.0f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = StreamViolet, activeTrackColor = StreamViolet)
                )
            }
        }

        // Section 2: Transcoding & ABR
        item {
            ConfigSection(
                title = "2. Transcoding Engine & ABR Ladder",
                subtitle = "Multi-bitrate encoding and CMAF segment duration",
                icon = Icons.Default.Dns
            ) {
                Text("Transcoding Acceleration Engine", color = StreamTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                TranscodingEngine.values().forEach { engine ->
                    val isSelected = config.transcodingEngine == engine
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StreamSurfaceElevated else Color.Transparent)
                            .clickable { viewModel.updatePipelineConfig(config.copy(transcodingEngine = engine)) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(engine.label, color = if (isSelected) StreamCyan else StreamTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(engine.type, color = StreamTextMuted, fontSize = 11.sp)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.updatePipelineConfig(config.copy(transcodingEngine = engine)) },
                            colors = RadioButtonDefaults.colors(selectedColor = StreamCyan)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Enable ABR Ladder", color = StreamTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("1080p, 720p, 480p, 360p profiles", color = StreamTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = config.enableAbrLadder,
                        onCheckedChange = { viewModel.updatePipelineConfig(config.copy(enableAbrLadder = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = StreamCyan)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("CMAF Segment Duration", color = StreamTextSecondary, fontSize = 12.sp)
                    Text("${String.format("%.1f", config.chunkDurationSec)}s chunks", color = StreamGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Slider(
                    value = config.chunkDurationSec,
                    onValueChange = { viewModel.updatePipelineConfig(config.copy(chunkDurationSec = it)) },
                    valueRange = 0.5f..4.0f,
                    steps = 6,
                    colors = SliderDefaults.colors(thumbColor = StreamGold, activeTrackColor = StreamGold)
                )
            }
        }

        // Section 3: CDN Delivery
        item {
            ConfigSection(
                title = "3. CDN Edge Delivery Mesh",
                subtitle = "Global caching, Origin Shielding, and edge PoPs",
                icon = Icons.Default.Public
            ) {
                Text("Edge CDN Provider", color = StreamTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CdnProvider.values().forEach { provider ->
                        val isSelected = config.cdnProvider == provider
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updatePipelineConfig(config.copy(cdnProvider = provider)) },
                            label = { Text(provider.label.split(" ").first(), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StreamGreen,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Origin Shielding Layer", color = StreamTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Reduces master transcode origin egress by 90%", color = StreamTextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = config.originShieldEnabled,
                        onCheckedChange = { viewModel.updatePipelineConfig(config.copy(originShieldEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = StreamGreen)
                    )
                }
            }
        }

        // Section 4: Playback Protocols
        item {
            ConfigSection(
                title = "4. Client Playback Engine",
                subtitle = "Delivery protocol to player application",
                icon = Icons.Default.PlayCircle
            ) {
                PlaybackProtocol.values().forEach { protocol ->
                    val isSelected = config.playbackProtocol == protocol
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) StreamSurfaceElevated else Color.Transparent)
                            .clickable { viewModel.updatePipelineConfig(config.copy(playbackProtocol = protocol)) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(protocol.label, color = if (isSelected) StreamCyan else StreamTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Typical Latency: ${protocol.latencyCategory}", color = StreamTextMuted, fontSize = 11.sp)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.updatePipelineConfig(config.copy(playbackProtocol = protocol)) },
                            colors = RadioButtonDefaults.colors(selectedColor = StreamCyan)
                        )
                    }
                }
            }
        }

        // Section 5: Architecture Breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "PIPELINE SPECIFICATION EXPLAINER",
                        color = StreamCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    explanations.forEach { (heading, detail) ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = heading, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = detail, color = StreamTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigSection(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = StreamCyan, modifier = Modifier.size(20.dp))
                Column {
                    Text(text = title, color = StreamTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = subtitle, color = StreamTextMuted, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(StreamSurfaceElevated)
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, color = StreamTextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = if (highlight) StreamCyan else StreamTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
