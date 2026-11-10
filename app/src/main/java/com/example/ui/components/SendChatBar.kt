package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
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
import com.example.ui.theme.*

@Composable
fun SendChatBar(
    onSendMessage: (String) -> Unit,
    onSendReaction: (String) -> Unit,
    onOpenSuperChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    val reactions = listOf("🔥", "👏", "💜", "🚀", "🎮", "⚡", "🎉", "👑", "💯")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StreamSurfaceDark)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Quick reaction bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "React:",
                color = StreamTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            reactions.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(StreamSurfaceElevated)
                        .clickable { onSendReaction(emoji) }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 16.sp)
                }
            }
        }

        // Chat input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Super Chat button
            IconButton(
                onClick = onOpenSuperChat,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(StreamGold)
                    .testTag("super_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Super Chat / Donate",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Input field
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        "Send a message to live chat...",
                        color = StreamTextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("chat_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = StreamSurfaceCard,
                    unfocusedContainerColor = StreamSurfaceCard,
                    focusedTextColor = StreamTextPrimary,
                    unfocusedTextColor = StreamTextPrimary,
                    cursorColor = StreamCyan,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            // Send button
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text.trim())
                        text = ""
                    }
                },
                enabled = text.isNotBlank(),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (text.isNotBlank()) StreamCyan else StreamSurfaceElevated)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    tint = if (text.isNotBlank()) Color.Black else StreamTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
