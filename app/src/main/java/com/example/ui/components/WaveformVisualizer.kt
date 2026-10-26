package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TealPrimary

@Composable
fun WaveformVisualizer(
    isListening: Boolean,
    soundLevel: Float,
    modifier: Modifier = Modifier,
    barColor: Color = TealPrimary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )
    val wave3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w3"
    )

    val baseBars = listOf(wave1, wave2, wave3, wave2, wave1, wave3, wave2)

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        baseBars.forEach { waveFactor ->
            val heightFraction = if (isListening) {
                (waveFactor * 0.5f + soundLevel * 0.5f).coerceIn(0.15f, 1.0f)
            } else {
                0.15f
            }
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height((36 * heightFraction).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isListening) barColor else barColor.copy(alpha = 0.3f))
            )
        }
    }
}
