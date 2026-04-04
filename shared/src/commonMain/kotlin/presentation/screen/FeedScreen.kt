package presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import domain.model.StockTick
import presentation.theme.GreenBullish
import presentation.theme.RedBearish
import presentation.theme.TickerCMPFlowTheme
import presentation.viewmodel.StockViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FeedScreen(
    viewModel: StockViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onNavigateToDetail: (String) -> Unit
) {
    val ticks by viewModel.stockTicks.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()

    FeedContent(
        ticks = ticks,
        isConnected = isConnected,
        isTracking = isTracking,
        onToggleTracking = { viewModel.toggleTracking() },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    ticks: List<StockTick>,
    isConnected: Boolean,
    isTracking: Boolean,
    onToggleTracking: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onNavigateToDetail: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "LIVE TERMINAL", 
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = if (isConnected) GreenBullish else RedBearish,
                        modifier = Modifier.padding(start = 16.dp).size(10.dp)
                    ) {}
                },
                actions = {
                    Switch(
                        checked = isTracking,
                        onCheckedChange = { onToggleTracking() },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(ticks, key = { it.symbol }) { tick ->
                StockItem(
                    tick = tick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .animateItem() 
                        .clickable { onNavigateToDetail(tick.symbol) }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StockItem(
    tick: StockTick,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {
    with(sharedTransitionScope) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tick.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "symbol-${tick.symbol}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = "Real-time updates",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${tick.price}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "price-${tick.symbol}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = "${if (tick.changePercentage >= 0) "+" else ""}${tick.changePercentage}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (tick.changePercentage >= 0) GreenBullish else RedBearish
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun FeedContentPreview() {
    val mockTicks = listOf(
        StockTick("TSLA", 182.45, 2.45, 0L),
        StockTick("NVDA", 142.18, 5.12, 0L),
        StockTick("AAPL", 175.84, 1.10, 0L),
        StockTick("BTC", 68412.0, -0.54, 0L)
    )
    TickerCMPFlowTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true) { _ ->
                FeedContent(
                    ticks = mockTicks,
                    isConnected = true,
                    isTracking = true,
                    onToggleTracking = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    onNavigateToDetail = {}
                )
            }
        }
    }
}
