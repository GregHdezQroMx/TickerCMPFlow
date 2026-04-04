package presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import presentation.screen.DetailScreen
import presentation.screen.FeedScreen
import presentation.viewmodel.StockDetailViewModel
import presentation.viewmodel.StockViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = Route.Feed
        ) {
            composable<Route.Feed> {
                val viewModel = koinViewModel<StockViewModel>()
                FeedScreen(
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onNavigateToDetail = { symbol ->
                        navController.navigate(Route.Details(symbol))
                    }
                )
            }

            composable<Route.Details> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.Details>()
                val viewModel = koinViewModel<StockDetailViewModel> { 
                    parametersOf(route.symbol) 
                }
                
                DetailScreen(
                    viewModel = viewModel,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
