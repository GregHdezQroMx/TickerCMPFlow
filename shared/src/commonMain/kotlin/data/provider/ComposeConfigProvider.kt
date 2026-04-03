package data.provider

import domain.provider.ConfigProvider
import org.jetbrains.compose.resources.getString
import tickercmpflow.shared.generated.resources.Res
import tickercmpflow.shared.generated.resources.symbols_list
import tickercmpflow.shared.generated.resources.ws_host
import tickercmpflow.shared.generated.resources.ws_path

/**
 * Implementation of ConfigProvider using JetBrains Compose Resources.
 * This keeps framework-specific logic out of the Repository.
 */
class ComposeConfigProvider : ConfigProvider {
    
    override suspend fun getHost(): String = getString(Res.string.ws_host)
    
    override suspend fun getPath(): String = getString(Res.string.ws_path)
    
    override suspend fun getSymbols(): List<String> {
        val rawSymbols = getString(Res.string.symbols_list)
        return rawSymbols.split(",").map { it.trim() }
    }
}