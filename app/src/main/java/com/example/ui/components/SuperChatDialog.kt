package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SuperChatDonation
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperChatDialog(
    streamerName: String,
    onDismiss: () -> Unit,
    onSendSuperChat: (SuperChatDonation) -> Unit
) {
    val tiers = listOf(
        Triple(2.0, 0xFF1E88E5, "Bronze Tier (30s Pin)"),
        Triple(5.0, 0xFF00ACC1, "Silver Tier (1m Pin)"),
        Triple(10.0, 0xFF43A047, "Gold Tier (2m Pin)"),
        Triple(20.0, 0xFFFB8C00, "Platinum Tier (5m Pin)"),
        Triple(50.0, 0xFFE53935, "Diamond Tier (10m Pin)"),
        Triple(100.0, 0xFFD81B60, "Champion Tier (30m Pin)")
    )

    var selectedIndex by remember { mutableIntStateOf(2) }
    var customMessage by remember { mutableStateOf("Huge play! Keep destroying them! 🏆🔥") }
    var userName by remember { mutableStateOf("Viewer_Prime") }

    val selectedTier = tiers[selectedIndex]
    val currentAmount = selectedTier.first
    val currentColor = Color(selectedTier.second)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = StreamSurfaceDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = StreamCyan) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StreamGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                    Column {
                        Text(
                            text = "Send Super Chat",
                            color = StreamTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Support $streamerName",
                            color = StreamTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = StreamTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Preview Card
            Text(
                text = "SUPER CHAT PREVIEW",
                color = StreamCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(currentColor)
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "$${String.format("%.2f", currentAmount)}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (customMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = customMessage,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tier selector buttons
            Text(
                text = "SELECT DONATION AMOUNT",
                color = StreamTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiers.take(3).forEachIndexed { index, (amount, colorHex, _) ->
                    val isSelected = selectedIndex == index
                    OutlinedButton(
                        onClick = { selectedIndex = index },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(colorHex).copy(alpha = 0.25f) else StreamSurfaceCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(colorHex) else Color(0xFF2E384D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$${amount.toInt()}",
                            color = if (isSelected) Color.White else StreamTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tiers.drop(3).forEachIndexed { i, (amount, colorHex, _) ->
                    val index = i + 3
                    val isSelected = selectedIndex == index
                    OutlinedButton(
                        onClick = { selectedIndex = index },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(colorHex).copy(alpha = 0.25f) else StreamSurfaceCard
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(colorHex) else Color(0xFF2E384D)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$${amount.toInt()}",
                            color = if (isSelected) Color.White else StreamTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Message field
            OutlinedTextField(
                value = customMessage,
                onValueChange = { customMessage = it },
                label = { Text("Super Chat Message", color = StreamTextSecondary) },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("super_chat_message_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = StreamTextPrimary,
                    unfocusedTextColor = StreamTextPrimary,
                    focusedBorderColor = StreamCyan,
                    unfocusedBorderColor = Color(0xFF2E384D)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Confirm Button
            Button(
                onClick = {
                    val donation = SuperChatDonation(
                        id = "sc_${System.currentTimeMillis()}",
                        senderName = userName,
                        amount = currentAmount,
                        message = customMessage.trim(),
                        colorHex = selectedTier.second,
                        timestamp = "Just now",
                        durationSeconds = (currentAmount * 15).toInt().coerceAtLeast(60)
                    )
                    onSendSuperChat(donation)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_super_chat_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StreamCyan
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Send Super Chat • $${String.format("%.2f", currentAmount)}",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
