package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.StreamUiState
import com.example.viewmodel.StreamViewModel

@Composable
fun CreatorMonetizationScreen(
    viewModel: StreamViewModel,
    uiState: StreamUiState,
    modifier: Modifier = Modifier
) {
    val earnings = uiState.creatorEarnings

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StreamBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Revenue Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().testTag("creator_revenue_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1B2338), Color(0xFF141926))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESTIMATED TOTAL REVENUE",
                                color = StreamCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StreamGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("+18.4% this month", color = StreamGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "$${String.format("%,.2f", earnings.totalRevenue)}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Revenue Split Breakdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RevenuePill(
                                label = "Super Chats",
                                amount = "$${String.format("%.0f", earnings.superChatsTotal)}",
                                color = StreamGold,
                                modifier = Modifier.weight(1f)
                            )
                            RevenuePill(
                                label = "Subscriptions",
                                amount = "$${String.format("%.0f", earnings.subscriptionsTotal)}",
                                color = StreamViolet,
                                modifier = Modifier.weight(1f)
                            )
                            RevenuePill(
                                label = "Bits & Gifts",
                                amount = "$${String.format("%.0f", earnings.bitsGemsTotal)}",
                                color = StreamCyan,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Channel Growth Metrics
        item {
            Text(
                text = "STREAM ANALYTICS & REACH",
                color = StreamCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnalyticsStatCard(
                    title = "Active Subscribers",
                    value = "${earnings.activeSubscribers}",
                    subtitle = "Tier 1: 310 • Tier 2: 60 • Tier 3: 14",
                    icon = Icons.Default.Loyalty,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "Peak Viewers (CCV)",
                    value = "${earnings.peakViewers}",
                    subtitle = "During Cyber Strike Finals",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subscription Tiers Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = StreamViolet)
                        Text(
                            text = "CHANNEL SUBSCRIPTION TIERS",
                            color = StreamTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    SubscriptionTierRow(
                        tierName = "Tier 1: Core Fan",
                        price = "$4.99/mo",
                        perks = "Custom emotes, sub badge in chat, ad-free viewing",
                        color = StreamCyan
                    )

                    HorizontalDivider(color = Color(0xFF2E384D))

                    SubscriptionTierRow(
                        tierName = "Tier 2: Super Fan",
                        price = "$9.99/mo",
                        perks = "All Tier 1 perks + VIP badge, priority chat color, Discord role",
                        color = StreamViolet
                    )

                    HorizontalDivider(color = Color(0xFF2E384D))

                    SubscriptionTierRow(
                        tierName = "Tier 3: Champion",
                        price = "$24.99/mo",
                        perks = "All perks + custom sound alerts on stream, monthly creator hangout",
                        color = StreamGold
                    )
                }
            }
        }

        // Recent Super Chat Supporters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = StreamLiveRed)
                            Text(
                                text = "RECENT SUPER CHAT DONATIONS",
                                color = StreamTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Text("${uiState.pinnedSuperChats.size} recent", color = StreamTextMuted, fontSize = 11.sp)
                    }

                    uiState.pinnedSuperChats.forEach { sc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(StreamSurfaceElevated)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sc.senderName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (sc.message.isNotBlank()) {
                                    Text(sc.message, color = StreamTextSecondary, fontSize = 11.sp, maxLines = 1)
                                }
                            }
                            Text(
                                text = "+$${String.format("%.2f", sc.amount)}",
                                color = Color(sc.colorHex),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Payout Settings Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StreamGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = StreamGreen, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Direct Payout Method", color = StreamTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Stripe Connected • Automatic Monthly Payout", color = StreamTextMuted, fontSize = 11.sp)
                        }
                    }
                    Text("ACTIVE", color = StreamGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun RevenuePill(
    label: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(StreamSurfaceElevated)
            .padding(8.dp)
    ) {
        Column {
            Text(label, color = StreamTextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(amount, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = StreamCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = StreamTextSecondary, fontSize = 11.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = StreamTextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun SubscriptionTierRow(
    tierName: String,
    price: String,
    perks: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(tierName, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(perks, color = StreamTextSecondary, fontSize = 11.sp)
        }
        Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
