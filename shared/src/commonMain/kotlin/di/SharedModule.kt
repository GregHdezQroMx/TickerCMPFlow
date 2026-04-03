package di

import com.jght.business.stockmarket.ticker_cmp_flow.data.repository.StockRepositoryImpl
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

    // HttpClient with WebSockets for the ticker
    single {
        HttpClient {
            install(WebSockets)
        }
    }

    // Repository
   factory<StockRepository> { (host: String, path: String) ->
       StockRepositoryImpl(
           client = get(),
           json = get(),
           baseUrl = host,
           apiPath = path
       )
    }

    // Domain Use Cases
    factory { GetStockUpdatesUseCase(repository = get()) }

    // ViewModel Factory
    viewModel { StockViewModel(getStockUpdatesUseCase = get()) }
}