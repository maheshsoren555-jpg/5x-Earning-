package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StreamCyan
import com.example.ui.theme.StreamGreen
import com.example.ui.theme.StreamLiveRed
import kotlin.random.Random

@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    barCount: Int = 12,
    isLive: Boolean = true,
    height: Dp = 24.dp
) {
    val transition = rememberInfiniteTransition(label = "audio_bars")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .height(height)
            .width((barCount * 6).dp)
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        val barWidth = (totalWidth / barCount) * 0.7f
        val spacing = (totalWidth / barCount) * 0.3f

        for (i in 0 until barCount) {
            val randomFactor = if (isLive) {
                // Generate dynamic height based on sine waves with pseudo noise
                val sinVal = kotlin.math.sin((phase * 2 * Math.PI + (i * 0.5f))).toFloat()
                val normalized = ((sinVal + 1f) / 2f).coerceIn(0.15f, 1.0f)
                val noise = ((i % 3) * 0.1f)
                (normalized * 0.85f + noise).coerceIn(0.1f, 1.0f)
            } else {
                0.05f
            }

            val barHeight = canvasHeight * randomFactor
            val top = canvasHeight - barHeight
            val left = i * (barWidth + spacing)

            val color = when {
                randomFactor > 0.85f -> StreamLiveRed
                randomFactor > 0.6f -> StreamCyan
                else -> StreamGreen
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
