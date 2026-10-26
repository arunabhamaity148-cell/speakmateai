package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.data.model.SpeechState
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WarningAmber

@Composable
fun MicButton(
    speechState: SpeechState,
    soundLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val isListening = speechState == SpeechState.LISTENING
    val isProcessing = speechState == SpeechState.PROCESSING

    val animatedScale = if (isListening) pulseScale + (soundLevel * 0.2f) else 1.0f

    val description = when (speechState) {
        SpeechState.IDLE -> "Tap to speak in English"
        SpeechState.LISTENING -> "Listening to your voice. Tap to stop."
        SpeechState.PROCESSING -> "Processing your speech. Please wait."
        SpeechState.SUCCESS -> "Speech recognized. Tap to speak again."
        SpeechState.ERROR -> "Error recognizing speech. Tap to retry."
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(96.dp)
            .semantics { contentDescription = description }
            .testTag("microphone_button")
    ) {
        // Outer pulsing halo
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.25f))
            )
        }

        // Main Button Surface
        val brush = when (speechState) {
            SpeechState.LISTENING -> Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
            SpeechState.PROCESSING -> Brush.linearGradient(listOf(WarningAmber, Color(0xFFD97706)))
            SpeechState.ERROR -> Brush.linearGradient(listOf(Color(0xFF64748B), Color(0xFF475569)))
            else -> Brush.linearGradient(listOf(TealPrimary, CyanSecondary))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .shadow(elevation = if (isListening) 12.dp else 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(brush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, radius = 40.dp),
                    onClick = onClick
                )
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                val icon = when (speechState) {
                    SpeechState.LISTENING -> Icons.Default.Stop
                    SpeechState.ERROR -> Icons.Default.Refresh
                    else -> Icons.Default.Mic
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}
