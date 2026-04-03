package domain.provider

/**
 * Interface to provide app-wide configurations.
 * Using suspend functions because resource fetching in KMP can be asynchronous.
 */
interface ConfigProvider {
    suspend fun getHost(): String
    suspend fun getPath(): String
    suspend fun getSymbols(): List<String>
}