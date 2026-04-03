package di

import com.jght.business.stockmarket.ticker_cmp_flow.data.repository.StockRepositoryImpl
import data.provider.ComposeConfigProvider
import domain.provider.ConfigProvider
import domain.repository.StockRepository
import domain.usecase.GetStockUpdatesUseCase
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
     * Provider implementation for Configuration (Framework/Data layer).
     */
    single<ConfigProvider> { ComposeConfigProvider() }

    /**
     * Singleton Repository: 
     * Now clean and decoupled via ConfigProvider.
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

    /**
     * ViewModel: 
     * Pure and decoupled.
     */
    viewModel { 
        StockViewModel(
            repository = get(),
            getStockUpdatesUseCase = get()
        )
    }
}