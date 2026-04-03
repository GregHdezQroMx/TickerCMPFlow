package di

import android.content.Context
import com.jght.business.stockmarket.ticker_cmp_flow.shared.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.koin.core.context.startKoin
import org.koin.core.module.Module

actual class KoinInitializer(private val context: Context) {
    actual fun init(additionalModules: List<Module>) {
        startKoin {
            // Provides Android Context to Koin
            androidContext(context)

            // Optional: Koin internal logging
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)

            // Infrastructure: Napier Setup inside the Shared Module
            if (BuildConfig.DEBUG) {
                Napier.base(DebugAntilog())
            }

            // sharedModule must be defined in your commonMain DI folder
            modules(sharedModule + additionalModules)
        }
    }
}