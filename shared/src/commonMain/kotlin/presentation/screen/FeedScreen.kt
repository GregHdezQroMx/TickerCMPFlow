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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.aakira.napier.Napier
import presentation.component.ConnectionBadge
import presentation.component.EmptyFeedState
import presentation.component.NetworkErrorBanner
import presentation.component.StockItemShimmer
import presentation.theme.GreenBullish
import presentation.theme.RedBearish
import presentation.theme.TickerCMPFlowTheme
import presentation.viewmodel.StockItemState
import presentation.viewmodel.StockListState
import presentation.viewmodel.StockViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FeedScreen(
    viewModel: StockViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onNavigateToDetail: (String) -> Unit
) {
    // Phase 5 Performance: Use the immutable wrapped state
    val listState by viewModel.stockListState.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val isReconnecting by viewModel.isReconnecting.collectAsState()
    val isPersistentError by viewModel.isPersistentError.collectAsState()

    FeedContent(
        listState = listState,
        isConnected = isConnected,
        isTracking = isTracking,
        isReconnecting = isReconnecting,
        isPersistentError = isPersistentError,
        onToggleTracking = { viewModel.toggleTracking() },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedContent(
    listState: StockListState,
    isConnected: Boolean,
    isTracking: Boolean,
    isReconnecting: Boolean,
    isPersistentError: Boolean,
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
                        text = "LIVE TERMINAL", 
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    ConnectionBadge(
                        isConnected = isConnected,
                        isReconnecting = isReconnecting,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                actions = {
                    Switch(
                        checked = isTracking,
                        onCheckedChange = { onToggleTracking() },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            NetworkErrorBanner(
                isVisible = isTracking && (!isConnected || isReconnecting),
                isReconnecting = isReconnecting,
                isPersistentError = isPersistentError
            )

            when {
                isTracking -> {
                    if (listState.items.isNotEmpty()) {
                        // PHASE 5: UI Stability Wrapper Call
                        StockList(
                            items = listState.items,
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onNavigateToDetail = onNavigateToDetail
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            userScrollEnabled = false
                        ) {
                            items(5) {
                                StockItemShimmer()
                            }
                        }
                    }
                }
                
                else -> {
                    EmptyFeedState()
                }
            }
        }
    }
}

/**
 * PHASE 5: STABILITY WRAPPER
 * Extracts the LazyColumn to a standalone Composable.
 * Since 'items' is now part of an @Immutable StockListState,
 * Compose can officially SKIP recomposing this entire list if no data changed.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun StockList(
    items: List<StockItemState>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onNavigateToDetail: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = items, 
            key = { it.tick.symbol } 
        ) { item ->
            StockItem(
                item = item,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .animateItem() 
                    .clickable { onNavigateToDetail(item.tick.symbol) }
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StockItem(
    item: StockItemState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {
    // Performance Tracking
    val recompositionCount = remember { mutableListOf<Int>() }
    SideEffect {
        recompositionCount.add(1)
        if (recompositionCount.size % 5 == 0) {
            Napier.v(tag = "PERF") { "⚡ Item [${item.tick.symbol}] recomposed ${recompositionCount.size} times" }
        }
    }

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.tick.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "symbol-${item.tick.symbol}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = item.companyName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${item.tick.price}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "price-${item.tick.symbol}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = "${if (item.tick.changePercentage >= 0) "+" else ""}${item.tick.changePercentage}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.tick.changePercentage >= 0) GreenBullish else RedBearish
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
    TickerCMPFlowTheme {
        SharedTransitionLayout {
            AnimatedContent(targetState = true) { _ ->
                FeedContent(
                    listState = StockListState(),
                    isConnected = true,
                    isTracking = true,
                    isReconnecting = false,
                    isPersistentError = false,
                    onToggleTracking = {},
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent,
                    onNavigateToDetail = {}
                )
            }
        }
    }
}
