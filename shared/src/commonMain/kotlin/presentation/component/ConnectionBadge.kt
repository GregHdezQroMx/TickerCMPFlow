package presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.theme.GreenBullish
import presentation.theme.RedBearish

private val AmberReconnecting = Color(0xFFFFB300)

/**
 * Reusable Connection Badge for the entire application.
 * Supports LIVE, OFFLINE, and RECONNECTING states for Phase 4 Hardening.
 */
@Composable
fun ConnectionBadge(
    isConnected: Boolean,
    isReconnecting: Boolean = false,
    modifier: Modifier = Modifier
) {
    val color = when {
        isConnected -> GreenBullish
        isReconnecting -> AmberReconnecting
        else -> RedBearish
    }
    
    val text = when {
        isConnected -> "LIVE"
        isReconnecting -> "RETRYING"
        else -> "OFFLINE"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "badgePulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = color.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .then(if (isReconnecting) Modifier.alpha(alpha) else Modifier)
                    .background(color, CircleShape)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = color
            )
        }
    }
}
