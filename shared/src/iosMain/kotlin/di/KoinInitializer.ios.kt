package di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KoinInitializer {
    // Note: No 'context' here because iOS doesn't have one
    actual fun init(additionalModules: List<Module>) {
        startKoin {
            // Infrastructure Setup for iOS
            // In KMP, we usually check if it's a debug build via custom flags
            // or just leave it active for the dev phase.
            Napier.base(DebugAntilog())

            modules(sharedModule + additionalModules)
        }
    }
}