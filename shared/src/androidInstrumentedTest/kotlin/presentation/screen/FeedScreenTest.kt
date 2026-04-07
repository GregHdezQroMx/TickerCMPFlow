package presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import presentation.theme.TickerCMPFlowTheme
import presentation.viewmodel.StockListState

@OptIn(ExperimentalSharedTransitionApi::class)
@RunWith(AndroidJUnit4::class)
class FeedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun terminalHeaderIsDisplayed() {
        composeTestRule.setContent {
            TickerCMPFlowTheme(darkTheme = true) {
                SharedTransitionLayout {
                    AnimatedContent(targetState = true) { _ ->
                        FeedContent(
                            listState = StockListState(),
                            isConnected = true,
                            isTracking = true,
                            isReconnecting = false,
                            isPersistentError = false,
                            isDarkTheme = true,
                            onToggleTracking = {},
                            onToggleTheme = {},
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@AnimatedContent,
                            onNavigateToDetail = {}
                        )
                    }
                }
            }
        }

        // Validate core elements via test tags
        composeTestRule.onNodeWithTag("tracking_switch").assertIsDisplayed()
    }
}
