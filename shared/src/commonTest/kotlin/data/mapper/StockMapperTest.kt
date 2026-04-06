package data.mapper

import domain.model.StockTick
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Technical Challenge Validation: StockMapper precision and resilience.
 * Ensures the trading terminal handles data with financial-grade accuracy.
 * Convention: Using CamelCase for cross-platform compatibility with Kotlin/Native.
 */
class StockMapperTest {

    @Test
    fun testParseValidWireFormatCorrectly() {
        // GIVEN: A raw string from the WebSocket
        val raw = "AAPL,150.12,1.5|MSFT,250.0,-0.5"
        
        // WHEN: Parsing to domain models
        val ticks = raw.toStockTicks()

        // THEN: Content is accurate
        assertEquals(2, ticks.size)
        assertEquals("AAPL", ticks[0].symbol)
        assertEquals(150.12, ticks[0].price)
        assertEquals(1.5, ticks[0].changePercentage)
        
        assertEquals("MSFT", ticks[1].symbol)
        assertEquals(250.0, ticks[1].price)
        assertEquals(-0.5, ticks[1].changePercentage)
    }

    @Test
    fun testEnforceDecimalPrecisionRounding() {
        // GIVEN: Prices with excessive decimals (Typical in raw financial streams)
        val raw = "NVDA,120.339,1.255"
        
        // WHEN: Mapping
        val ticks = raw.toStockTicks()

        // THEN: Prices must be rounded to 2 decimals
        assertEquals(120.34, ticks[0].price, "Price should be rounded to 2 decimals")
        assertEquals(1.26, ticks[0].changePercentage, "Percentage should be rounded to 2 decimals")
    }

    @Test
    fun testHandleMalformedSegmentsGracefully() {
        // GIVEN: A string with corrupt data in the middle
        val raw = "AAPL,150.0,1.0|CORRUPT_DATA_HERE|GOOGL,2800.0,0.5"
        
        // WHEN: Parsing
        val ticks = raw.toStockTicks()

        // THEN: Invalid segments are ignored, valid ones are kept
        assertEquals(2, ticks.size)
        assertEquals("AAPL", ticks[0].symbol)
        assertEquals("GOOGL", ticks[1].symbol)
    }

    @Test
    fun testReturnEmptyListForBlankInputs() {
        assertTrue("".toStockTicks().isEmpty())
        assertTrue("   ".toStockTicks().isEmpty())
    }

    @Test
    fun testToWireFormatProducesCorrectString() {
        val ticks = listOf(
            StockTick("BTC", 45000.12, 2.5, 0L),
            StockTick("ETH", 2500.55, -1.2, 0L)
        )
        val expected = "BTC,45000.12,2.5|ETH,2500.55,-1.2"
        assertEquals(expected, ticks.toWireFormat())
    }
}
