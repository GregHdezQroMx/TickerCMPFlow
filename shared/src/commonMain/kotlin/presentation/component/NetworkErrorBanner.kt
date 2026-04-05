package presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import presentation.theme.RedBearish

private val AmberRetry = Color(0xFFFFB300)

@Composable
fun NetworkErrorBanner(
    isVisible: Boolean,
    isReconnecting: Boolean,
    isPersistentError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isPersistentError -> RedBearish
        isReconnecting -> AmberRetry
        else -> RedBearish
    }

    val message = when {
        isPersistentError -> "🚨 Persistent connection issue. Still monitoring..."
        isReconnecting -> "Network unstable. Retrying connection..."
        else -> "No internet connection. Please check your network."
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor.copy(alpha = 0.9f))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
