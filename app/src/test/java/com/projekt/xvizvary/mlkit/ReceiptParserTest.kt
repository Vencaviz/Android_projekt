package com.projekt.xvizvary.mlkit

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ReceiptParserTest {

    private lateinit var parser: ReceiptParser

    @Before
    fun setup() {
        parser = ReceiptParser()
    }

    @Test
    fun `parse extracts store name from first lines`() {
        val ocrText = """
            ALBERT HYPERMARKET
            Vídeňská 89, Brno
            IČO: 12345678
            
            Mléko 1l        25,90
            Chléb           32,00
            
            CELKEM         57,90 Kč
        """.trimIndent()

        val result = parser.parse(ocrText)

        assertNotNull(result.storeName)
        assertTrue(result.storeName!!.contains("ALBERT", ignoreCase = true))
    }

    @Test
    fun `parse extracts total amount with Czech format`() {
        val ocrText = """
            Kaufland
            Položka 1       100,00
            Položka 2       250,50
            
            Celkem: 350,50 Kč
        """.trimIndent()

        val result = parser.parse(ocrText)

        assertNotNull(result.totalAmount)
        assertEquals(350.50, result.totalAmount!!, 0.01)
    }

    @Test
    fun `parse extracts total amount with K úhradě format`() {
        val ocrText = """
            Billa
            Jablka          45,00
            Banány          38,00
            
            K úhradě: 83,00
        """.trimIndent()

        val result = parser.parse(ocrText)

        assertNotNull(result.totalAmount)
        assertEquals(83.0, result.totalAmount!!, 0.01)
    }

    @Test
    fun `parse extracts date in DD_MM_YYYY format`() {
        val ocrText = """
            Lidl
            Datum: 15.03.2024
            Položka         99,00
            Celkem          99,00
        """.trimIndent()

        val result = parser.parse(ocrText)

        assertNotNull(result.date)
        assertEquals("15.03.2024", result.date)
    }

    @Test
    fun `parse handles English total format`() {
        val ocrText = """
            Store Name
            Item 1          10.00
            Item 2          20.00
            
            Total: 30.00
        """.trimIndent()

        val result = parser.parse(ocrText)

        assertNotNull(result.totalAmount)
        assertEquals(30.0, result.totalAmount!!, 0.01)
    }

    @Test
    fun `parse returns raw text`() {
        val ocrText = "Test receipt text"

        val result = parser.parse(ocrText)

        assertEquals(ocrText, result.rawText)
    }

    @Test
    fun `getConfidenceScore returns high score for complete data`() {
        val receipt = ParsedReceipt(
            storeName = "Albert",
            totalAmount = 150.0,
            date = "01.01.2024",
            rawText = "test"
        )

        val score = parser.getConfidenceScore(receipt)

        assertEquals(1.0f, score, 0.01f)
    }

    @Test
    fun `getConfidenceScore returns lower score for partial data`() {
        val receipt = ParsedReceipt(
            storeName = null,
            totalAmount = 150.0,
            date = null,
            rawText = "test"
        )

        val score = parser.getConfidenceScore(receipt)

        assertEquals(0.5f, score, 0.01f) // Only amount present
    }
}
