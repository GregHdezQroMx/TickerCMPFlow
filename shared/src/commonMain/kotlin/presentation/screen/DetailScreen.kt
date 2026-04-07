package presentation.screen

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import domain.model.Candle
import domain.model.StockMetadata
import kotlinx.coroutines.delay
import presentation.component.ConnectionBadge
import presentation.component.NetworkErrorBanner
import presentation.theme.GreenBullish
import presentation.theme.RedBearish
import presentation.viewmodel.StockDetailViewModel

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: StockDetailViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope,
    onBack: () -> Unit
) {
    // Phase 5 Bonus: Lifecycle-aware collection
    val tick by viewModel.stockTick.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val isReconnecting by viewModel.isReconnecting.collectAsStateWithLifecycle()
    val isPersistentError by viewModel.isPersistentError.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    
    val metadata = viewModel.metadata
    val scrollState = rememberScrollState()

    // BONUS: Elegant Text-Only Price Flashing (1s duration)
    val defaultColor = MaterialTheme.colorScheme.onSurface
    val priceColor = remember { Animatable(defaultColor) }
    var prevPrice by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(tick?.price) {
        val currentPrice = tick?.price ?: 0.0
        val oldPrice = prevPrice
        if (oldPrice != null && currentPrice != 0.0 && currentPrice != oldPrice) {
            val flashColor = if (currentPrice > oldPrice) GreenBullish else RedBearish
            priceColor.snapTo(flashColor)
            priceColor.animateTo(defaultColor, tween(1000))
        }
        prevPrice = currentPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ConnectionBadge(
                            isConnected = isConnected,
                            isReconnecting = isReconnecting
                        )
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Switch(
                        checked = isTracking,
                        onCheckedChange = { viewModel.toggleTracking() },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                StaggeredItem(index = 4) {
                    Button(
                        onClick = { /* Buy Action */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "BUY ${metadata.symbol}", 
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            NetworkErrorBanner(
                isVisible = isTracking && (!isConnected || isReconnecting),
                isReconnecting = isReconnecting,
                isPersistentError = isPersistentError
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
            ) {
                with(sharedTransitionScope) {
                    Row(
                        modifier = Modifier.padding(top = 24.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = metadata.symbol,
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.sharedElement(
                                    rememberSharedContentState(key = "symbol-${metadata.symbol}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            )
                            Text(
                                text = metadata.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        tick?.let {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${it.price}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = priceColor.value, // APPLIED ANIMATION TO TEXT
                                    modifier = Modifier.sharedElement(
                                        rememberSharedContentState(key = "price-${metadata.symbol}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                )
                                Text(
                                    text = "${if (it.changePercentage >= 0) "+" else ""}${it.changePercentage}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (it.changePercentage >= 0) GreenBullish else RedBearish
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                StaggeredItem(index = 1) {
                    CandlestickChart(history)
                }

                StaggeredItem(index = 2) {
                    AboutSection(metadata)
                }

                StaggeredItem(index = 3) {
                    MarketStats(metadata)
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun CandlestickChart(history: List<Candle>) {
    Column {
        Text(
            text = "MARKET PERFORMANCE", 
            style = MaterialTheme.typography.titleSmall, 
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(220.dp).padding(vertical = 8.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (history.isEmpty()) return@Canvas
                val maxHigh = history.maxOf { it.high }.toFloat()
                val minLow = history.minOf { it.low }.toFloat()
                val range = maxHigh - minLow
                val candleWidth = size.width / history.size
                val space = 4.dp.toPx()
                history.forEachIndexed { index, candle ->
                    val color = if (candle.close >= candle.open) GreenBullish else RedBearish
                    val x = index * candleWidth + (space / 2)
                    val width = candleWidth - space
                    val yHigh = ((maxHigh - candle.high.toFloat()) / range) * size.height
                    val yLow = ((maxHigh - candle.low.toFloat()) / range) * size.height
                    val yOpen = ((maxHigh - candle.open.toFloat()) / range) * size.height
                    val yClose = ((maxHigh - candle.close.toFloat()) / range) * size.height
                    drawLine(color, Offset(x + width / 2, yHigh), Offset(x + width / 2, yLow), 2.dp.toPx())
                    val top = minOf(yOpen, yClose)
                    val bottom = maxOf(yOpen, yClose)
                    drawRect(color, Offset(x, top), Size(width, maxOf(2.dp.toPx(), bottom - top)))
                }
            }
        }
    }
}

@Composable
private fun AboutSection(metadata: StockMetadata) {
    Column {
        Text("ABOUT ${metadata.name}", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = metadata.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun MarketStats(metadata: StockMetadata) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatItem("Market Cap", metadata.marketCap)
            StatItem("P/E Ratio", metadata.peRatio)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = label, 
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(text = value, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun StaggeredItem(index: Int, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(600))
    ) { content() }
}
