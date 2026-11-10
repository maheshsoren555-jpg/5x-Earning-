package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LiveStream
import com.example.ui.components.LiveBadge
import com.example.ui.theme.*
import com.example.viewmodel.StreamUiState
import com.example.viewmodel.StreamViewModel

@Composable
fun BrowseStreamsScreen(
    viewModel: StreamViewModel,
    uiState: StreamUiState,
    onSelectStream: (LiveStream) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf(uiState.searchQuery) }
    val categories = listOf("All", "Esports", "Software & Tech", "Music & Creative", "IRL Travel")

    val filteredStreams = uiState.allStreams.filter { stream ->
        val matchesCategory = uiState.selectedCategory == "All" || stream.category == uiState.selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                stream.title.contains(searchQuery, ignoreCase = true) ||
                stream.streamerName.contains(searchQuery, ignoreCase = true) ||
                stream.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StreamBgDark),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Bar
        item {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.setSearchQuery(it)
                },
                placeholder = { Text("Search streamers, games, or tags...", color = StreamTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = StreamCyan) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = ""; viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = StreamTextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("search_streams_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = StreamSurfaceCard,
                    unfocusedContainerColor = StreamSurfaceCard,
                    focusedTextColor = StreamTextPrimary,
                    unfocusedTextColor = StreamTextPrimary,
                    cursorColor = StreamCyan,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(26.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StreamCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = StreamSurfaceCard,
                            labelColor = StreamTextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Featured Stream Hero Card
        if (filteredStreams.isNotEmpty()) {
            val featured = filteredStreams.first()
            item {
                Text(
                    text = "FEATURED LIVE STREAM",
                    color = StreamCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectStream(featured) }
                        .testTag("featured_stream_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                        ) {
                            if (featured.previewDrawableRes != null) {
                                Image(
                                    painter = painterResource(id = featured.previewDrawableRes),
                                    contentDescription = featured.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(StreamSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = StreamCyan, modifier = Modifier.size(48.dp))
                                }
                            }

                            // Overlays
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xCC090B10))
                                        )
                                    )
                            )

                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LiveBadge()
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Text("${featured.viewersCount} viewers", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(StreamCyan)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("WATCH NOW", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        // Stream Info Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(StreamViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = featured.streamerName.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = featured.title,
                                    color = StreamTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${featured.streamerName} • ${featured.category}",
                                    color = StreamTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Channels Header
        item {
            Text(
                text = "ALL LIVE CHANNELS (${filteredStreams.size})",
                color = StreamCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        // Channels List
        items(filteredStreams, key = { it.id }) { stream ->
            StreamCardItem(
                stream = stream,
                onClick = { onSelectStream(stream) }
            )
        }
    }
}

@Composable
fun StreamCardItem(
    stream: LiveStream,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("stream_channel_${stream.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = StreamSurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail box
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 75.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StreamSurfaceElevated)
            ) {
                if (stream.previewDrawableRes != null) {
                    Image(
                        painter = painterResource(id = stream.previewDrawableRes),
                        contentDescription = stream.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LiveTv, contentDescription = null, tint = StreamCyan, modifier = Modifier.size(28.dp))
                    }
                }

                LiveBadge(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                )

                Text(
                    text = "${stream.viewersCount}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            // Stream metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stream.title,
                    color = StreamTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stream.streamerName,
                    color = StreamCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stream.category,
                    color = StreamTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    stream.tags.take(2).forEach { tag ->
                        Text(
                            text = "#$tag",
                            color = StreamViolet,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(StreamSurfaceElevated, RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}
