package presentation.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.model.StockMetadata
import kotlinx.coroutines.delay
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
    val tick by viewModel.stockTick.collectAsState()
    val metadata = viewModel.metadata
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            with(sharedTransitionScope) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    tick?.let {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${it.price}",
                                style = MaterialTheme.typography.headlineMedium,
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
                MarketChartPlaceholder()
            }

            StaggeredItem(index = 2) {
                AboutSection(metadata)
            }

            StaggeredItem(index = 3) {
                MarketStats(metadata)
            }

            StaggeredItem(index = 4) {
                Button(
                    onClick = { /* Buy Logic */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("BUY ${metadata.symbol}", style = MaterialTheme.typography.titleMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun MarketChartPlaceholder() {
    Column {
        Text(
            text = "MARKET PERFORMANCE", 
            style = MaterialTheme.typography.labelLarge, 
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("🕯️ Candlestick Chart (Simulated)", color = GreenBullish)
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
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
            style = MaterialTheme.typography.labelMedium, 
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun StaggeredItem(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 30L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(400)
        )
    ) {
        content()
    }
}
