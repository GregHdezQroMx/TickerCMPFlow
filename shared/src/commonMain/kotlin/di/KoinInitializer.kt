package di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Platform-specific initializer for Koin and Infrastructure (Napier).
 * This ensures the UI module doesn't handle logging setup.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KoinInitializer {
    fun init(additionalModules: List<Module> = emptyList())
}