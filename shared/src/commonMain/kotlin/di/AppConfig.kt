package di

/**
 * Clean way to provide configuration without polluting constructors with many strings.
 */
data class AppConfig(
    val host: String,
    val path: String
)
