package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LiveBadge
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.StreamViewModel

enum class StreamPulseTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    WATCH("Watch", Icons.Default.PlayCircle, "tab_watch"),
    BROWSE("Browse", Icons.Default.Explore, "tab_browse"),
    PIPELINE("Pipeline Lab", Icons.Default.Tune, "tab_pipeline"),
    STUDIO("Studio Ingest", Icons.Default.Sensors, "tab_studio"),
    MONETIZATION("Earnings", Icons.Default.MonetizationOn, "tab_monetization")
}

class MainActivity : ComponentActivity() {

    private val viewModel: StreamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StreamPulseApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamPulseApp(viewModel: StreamViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(StreamPulseTab.WATCH) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBgDark),
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(StreamCyan, StreamViolet)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "StreamPulse",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                        LiveBadge(
                            text = if (uiState.broadcastStudioState.isBroadcasting) "ON AIR" else "LIVE",
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                },
                actions = {
                    // Quick Ingest latency chip
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(StreamSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LL-HLS • ${uiState.currentStream.latencyMs}ms",
                            color = StreamCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StreamSurfaceDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = StreamSurfaceDark,
                contentColor = StreamTextPrimary
            ) {
                StreamPulseTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StreamCyan,
                            selectedTextColor = StreamCyan,
                            indicatorColor = StreamSurfaceElevated,
                            unselectedIconColor = StreamTextSecondary,
                            unselectedTextColor = StreamTextSecondary
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                StreamPulseTab.WATCH -> {
                    WatchPlayerScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onNavigateToPipelineLab = { currentTab = StreamPulseTab.PIPELINE }
                    )
                }
                StreamPulseTab.BROWSE -> {
                    BrowseStreamsScreen(
                        viewModel = viewModel,
                        uiState = uiState,
                        onSelectStream = { stream ->
                            viewModel.selectStream(stream)
                            currentTab = StreamPulseTab.WATCH
                        }
                    )
                }
                StreamPulseTab.PIPELINE -> {
                    PipelineConfigScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
                StreamPulseTab.STUDIO -> {
                    BroadcastStudioScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
                StreamPulseTab.MONETIZATION -> {
                    CreatorMonetizationScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }
        }
    }
}
