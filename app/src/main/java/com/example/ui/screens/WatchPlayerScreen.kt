package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.StreamUiState
import com.example.viewmodel.StreamViewModel

@Composable
fun WatchPlayerScreen(
    viewModel: StreamViewModel,
    uiState: StreamUiState,
    onNavigateToPipelineLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSuperChatDialog by remember { mutableStateOf(false) }
    val chatListState = rememberLazyListState()

    // Auto-scroll chat to bottom as new messages arrive
    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StreamBgDark)
    ) {
        // Video Player Container
        LivePlayerView(
            stream = uiState.currentStream,
            selectedQuality = uiState.selectedQuality,
            onQualitySelected = { viewModel.setQuality(it) },
            qualityOptions = uiState.qualityOptions,
            reactions = uiState.activeReactions
        )

        // Stream Channel Header Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StreamSurfaceDark)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Streamer Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(StreamCyan, StreamViolet)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.currentStream.streamerName.take(2).uppercase(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.currentStream.title,
                        color = StreamTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = uiState.currentStream.streamerName,
                            color = StreamCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• ${uiState.currentStream.category}",
                            color = StreamTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Subscribe Button
            Button(
                onClick = { viewModel.toggleSubscription() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isSubscribedToCurrent) StreamSurfaceElevated else StreamViolet
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("subscribe_channel_button")
            ) {
                Icon(
                    imageVector = if (uiState.isSubscribedToCurrent) Icons.Default.Check else Icons.Default.Star,
                    contentDescription = null,
                    tint = if (uiState.isSubscribedToCurrent) StreamGreen else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (uiState.isSubscribedToCurrent) "Subscribed" else "Subscribe",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Pinned Super Chat Carousel
        if (uiState.pinnedSuperChats.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StreamSurfaceCard)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                uiState.pinnedSuperChats.take(2).forEach { sc ->
                    SuperChatBanner(
                        donation = sc,
                        onDismiss = { viewModel.dismissSuperChat(sc.id) }
                    )
                }
            }
        }

        // Live Chat Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StreamSurfaceDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = StreamCyan,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "LIVE STREAM CHAT",
                    color = StreamTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            // Quick shortcut to Pipeline Lab
            TextButton(
                onClick = onNavigateToPipelineLab,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = StreamCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Video Pipeline", color = StreamCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Chat Message Stream
        LazyColumn(
            state = chatListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(uiState.chatMessages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg)
            }
        }

        // Bottom Chat Input Bar
        SendChatBar(
            onSendMessage = { viewModel.sendMessage(it) },
            onSendReaction = { viewModel.sendReaction(it) },
            onOpenSuperChat = { showSuperChatDialog = true }
        )
    }

    if (showSuperChatDialog) {
        SuperChatDialog(
            streamerName = uiState.currentStream.streamerName,
            onDismiss = { showSuperChatDialog = false },
            onSendSuperChat = { viewModel.sendSuperChat(it) }
        )
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    if (message.superChat != null) {
        val sc = message.superChat
        val scColor = Color(sc.colorHex)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(scColor.copy(alpha = 0.2f))
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BadgeChip(badge = message.badge)
                Text(
                    text = message.userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "$${String.format("%.2f", sc.amount)}",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(scColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            if (sc.message.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sc.message,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BadgeChip(badge = message.badge)
            Text(
                text = "${message.userName}: ",
                color = when (message.badge) {
                    UserBadge.BROADCASTER -> StreamLiveRed
                    UserBadge.MODERATOR -> StreamGreen
                    UserBadge.VIP -> StreamViolet
                    UserBadge.SUBSCRIBER -> StreamGold
                    UserBadge.VIEWER -> StreamCyan
                },
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = message.message,
                color = StreamTextPrimary,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = message.timestamp,
                color = StreamTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BadgeChip(badge: UserBadge) {
    val (label, bg, fg) = when (badge) {
        UserBadge.BROADCASTER -> Triple("LIVE", StreamLiveRed, Color.White)
        UserBadge.MODERATOR -> Triple("MOD", StreamGreen, Color.Black)
        UserBadge.VIP -> Triple("VIP", StreamViolet, Color.White)
        UserBadge.SUBSCRIBER -> Triple("SUB", StreamGold, Color.Black)
        UserBadge.VIEWER -> Triple("FAN", StreamSurfaceElevated, StreamTextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
