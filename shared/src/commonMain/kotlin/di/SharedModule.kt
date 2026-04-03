package di

import data.provider.ComposeConfigProvider
import data.repository.StockRepositoryImpl
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import domain.usecase.GetStockUpdatesUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel
import presentation.viewmodel.StockViewModel

val sharedModule = module {

    // Standard Json configuration
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // HttpClient with WebSockets
    single {
        HttpClient {
            install(WebSockets)
        }
    }

    /**
     * Provider implementation for Configuration.
     */
    single<ConfigProvider> { ComposeConfigProvider() }

    /**
     * Singleton Repository: 
     * Manages raw WebSocket connection.
     */
    single<StockRepository> {
        StockRepositoryImpl(
            client = get(),
            json = get(),
            configProvider = get()
        )
    }

    // Domain Use Cases
    factory { GetStockUpdatesUseCase(repository = get()) }
    single { ToggleStockTrackingUseCase(repository = get(), configProvider = get()) }
    factory { ObserveConnectionStatusUseCase(repository = get()) }

    /**
     * ViewModel: 
     * Orchestrates UI using UseCases.
     */
    viewModel { 
        StockViewModel(
            getStockUpdatesUseCase = get(),
            toggleStockTrackingUseCase = get(),
            observeConnectionStatusUseCase = get()
        )
    }
}