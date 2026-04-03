package domain.usecase

import domain.model.StockMetadata

/**
 * UseCase to fetch descriptive information about a stock symbol.
 * Provides mock data for all symbols listed in symbols_list.
 */
class GetStockMetadataUseCase {
    operator fun invoke(symbol: String): StockMetadata {
        return when (symbol.uppercase()) {
            "AAPL" -> StockMetadata("AAPL", "Apple Inc.", "Global leader in consumer electronics, software, and services.", "3.1T", "31.2")
            "GOOGL" -> StockMetadata("GOOGL", "Alphabet Inc.", "Parent company of Google, specializing in internet-related services.", "2.1T", "27.5")
            "MSFT" -> StockMetadata("MSFT", "Microsoft Corp.", "Leading developer of personal computer software and cloud services.", "3.2T", "35.8")
            "AMZN" -> StockMetadata("AMZN", "Amazon.com, Inc.", "E-commerce giant and leader in cloud computing (AWS).", "1.9T", "52.4")
            "TSLA" -> StockMetadata("TSLA", "Tesla, Inc.", "Pioneer in electric vehicles, battery energy storage, and solar panels.", "750B", "48.2")
            "META" -> StockMetadata("META", "Meta Platforms", "Social technology company building the metaverse and owner of Facebook/Instagram.", "1.4T", "29.1")
            "NVDA" -> StockMetadata("NVDA", "NVIDIA Corp.", "Leader in AI computing and graphics processing units (GPUs).", "2.8T", "72.4")
            "NFLX" -> StockMetadata("NFLX", "Netflix, Inc.", "The world's leading streaming entertainment service.", "280B", "42.6")
            "PYPL" -> StockMetadata("PYPL", "PayPal Holdings", "A leading technology platform that enables digital payments.", "70B", "16.8")
            "BABA" -> StockMetadata("BABA", "Alibaba Group", "China-based multinational technology company specialized in e-commerce.", "200B", "13.4")
            "DIS" -> StockMetadata("DIS", "The Walt Disney Co.", "Massive media and entertainment conglomerate.", "195B", "24.5")
            "V" -> StockMetadata("V", "Visa Inc.", "Global payments technology company connecting consumers and businesses.", "580B", "31.2")
            "BTC" -> StockMetadata("BTC", "Bitcoin", "The first decentralized digital currency, acting as a store of value.", "1.4T", "N/A")
            "ETH" -> StockMetadata("ETH", "Ethereum", "Decentralized, open-source blockchain with smart contract functionality.", "450B", "N/A")
            "BNB" -> StockMetadata("BNB", "Binance Coin", "The native token of the Binance cryptocurrency exchange ecosystem.", "95B", "N/A")
            "SOL" -> StockMetadata("SOL", "Solana", "High-performance blockchain supporting smart contracts and dApps.", "85B", "N/A")
            "ADA" -> StockMetadata("ADA", "Cardano", "Proof-of-stake blockchain platform focused on sustainability and security.", "18B", "N/A")
            "DOT" -> StockMetadata("DOT", "Polkadot", "Protocol that connects blockchains, allowing them to interoperate.", "12B", "N/A")
            "MATIC" -> StockMetadata("MATIC", "Polygon", "Ethereum scaling platform that enables developers to build scalable dApps.", "9B", "N/A")
            "LINK" -> StockMetadata("LINK", "Chainlink", "Decentralized oracle network that provides real-world data to smart contracts.", "11B", "N/A")
            "AVAX" -> StockMetadata("AVAX", "Avalanche", "Platform for decentralized applications and custom blockchain networks.", "16B", "N/A")
            "LTC" -> StockMetadata("LTC", "Litecoin", "A peer-to-peer cryptocurrency intended to be a 'lighter' version of Bitcoin.", "7B", "N/A")
            "GOLD" -> StockMetadata("GOLD", "Gold", "Physical commodity traditionally used as a hedge against inflation.", "15T", "N/A")
            "SILVER" -> StockMetadata("SILVER", "Silver", "Precious metal with significant industrial and investment demand.", "1.6T", "N/A")
            "OIL" -> StockMetadata("OIL", "Crude Oil", "The world's most vital energy commodity and feedstock.", "2.2T", "N/A")
            else -> StockMetadata(symbol, symbol, "No additional information available for $symbol.")
        }
    }
}
