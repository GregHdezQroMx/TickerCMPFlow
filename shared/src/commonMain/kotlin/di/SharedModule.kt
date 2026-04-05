package di

import data.provider.ComposeConfigProvider
import data.repository.StockRepositoryImpl
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import domain.usecase.GetStockDetailUseCase
import domain.usecase.GetStockMetadataUseCase
import domain.usecase.GetStockUpdatesUseCase
import domain.usecase.ObserveConnectionStatusUseCase
import domain.usecase.ToggleStockTrackingUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.websocket.WebSockets
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import presentation.viewmodel.StockDetailViewModel
import presentation.viewmodel.StockViewModel

val sharedModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        HttpClient {
            install(WebSockets)
            // Senior Move: Log real binary/text frames to Logcat
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.v(tag = "HTTP_CLIENT") { message }
                    }
                }
            }
        }
    }

    single<ConfigProvider> { ComposeConfigProvider() }

    single<StockRepository> {
        StockRepositoryImpl(
            client = get(),
            json = get(),
            configProvider = get()
        )
    }

    factory { GetStockUpdatesUseCase(repository = get()) }
    single { ToggleStockTrackingUseCase(repository = get(), configProvider = get()) }
    factory { ObserveConnectionStatusUseCase(repository = get()) }
    factory { GetStockMetadataUseCase() }
    factory { GetStockDetailUseCase(repository = get()) }

    viewModel { 
        StockViewModel(
            getStockUpdatesUseCase = get(),
            toggleStockTrackingUseCase = get(),
            observeConnectionStatusUseCase = get(),
            getStockMetadataUseCase = get()
        )
    }

    viewModel { (symbol: String) ->
        StockDetailViewModel(
            symbol = symbol,
            getStockDetailUseCase = get(),
            getStockMetadataUseCase = get(),
            toggleStockTrackingUseCase = get(),
            observeConnectionStatusUseCase = get()
        )
    }
}
