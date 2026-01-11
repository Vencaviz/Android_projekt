package com.projekt.xvizvary.mlkit

import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class representing parsed receipt information
 */
data class ParsedReceipt(
    val storeName: String?,
    val totalAmount: Double?,
    val date: String?,
    val rawText: String
)

/**
 * Parser for extracting structured data from OCR text of receipts
 */
@Singleton
class ReceiptParser @Inject constructor() {

    // Patterns for matching total amount (Czech and English)
    private val totalPatterns = listOf(
        // Czech patterns
        Pattern.compile("(?i)celkem[:\\s]*([0-9]+[,.]?[0-9]*)\\s*(kč|czk|,-)?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)součet[:\\s]*([0-9]+[,.]?[0-9]*)\\s*(kč|czk|,-)?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)k\\s*úhradě[:\\s]*([0-9]+[,.]?[0-9]*)\\s*(kč|czk|,-)?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)total[:\\s]*([0-9]+[,.]?[0-9]*)\\s*(kč|czk|,-)?", Pattern.CASE_INSENSITIVE),
        // English patterns
        Pattern.compile("(?i)total[:\\s]*\\$?([0-9]+[,.]?[0-9]*)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)sum[:\\s]*\\$?([0-9]+[,.]?[0-9]*)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)amount[:\\s]*\\$?([0-9]+[,.]?[0-9]*)", Pattern.CASE_INSENSITIVE),
        // Generic large number pattern (fallback - finds largest number)
        Pattern.compile("([0-9]+[,.]?[0-9]{2})\\s*(kč|czk|,-|Kč)?")
    )

    // Patterns for date matching
    private val datePatterns = listOf(
        // DD.MM.YYYY or DD/MM/YYYY
        Pattern.compile("(\\d{1,2})[./](\\d{1,2})[./](\\d{4})"),
        Pattern.compile("(\\d{1,2})[./](\\d{1,2})[./](\\d{2})"),
        // YYYY-MM-DD
        Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})")
    )

    // Common store name indicators
    private val storeIndicators = listOf(
        "s.r.o.", "a.s.", "spol.", "inc.", "ltd.", "gmbh",
        "albert", "billa", "lidl", "kaufland", "tesco", "penny",
        "globus", "makro", "coop", "hruška", "žabka", "flop"
    )

    /**
     * Parse OCR text and extract receipt information
     */
    fun parse(ocrText: String): ParsedReceipt {
        val lines = ocrText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        
        return ParsedReceipt(
            storeName = extractStoreName(lines),
            totalAmount = extractTotalAmount(ocrText),
            date = extractDate(ocrText),
            rawText = ocrText
        )
    }

    /**
     * Extract store name - usually in first few lines of receipt
     */
    private fun extractStoreName(lines: List<String>): String? {
        // Check first 5 lines for store name
        val headerLines = lines.take(5)
        
        // First, look for known store indicators
        for (line in headerLines) {
            val lowerLine = line.lowercase()
            for (indicator in storeIndicators) {
                if (lowerLine.contains(indicator)) {
                    return cleanStoreName(line)
                }
            }
        }
        
        // If no indicator found, return first non-empty line that looks like a name
        for (line in headerLines) {
            if (line.length in 3..50 && !line.matches(Regex("^[0-9.,\\s]+$"))) {
                return cleanStoreName(line)
            }
        }
        
        return headerLines.firstOrNull()
    }

    /**
     * Clean up store name
     */
    private fun cleanStoreName(name: String): String {
        return name
            .replace(Regex("[^a-zA-ZáčďéěíňóřšťúůýžÁČĎÉĚÍŇÓŘŠŤÚŮÝŽ\\s.,&-]"), "")
            .trim()
            .take(50)
    }

    /**
     * Extract total amount from receipt
     */
    private fun extractTotalAmount(text: String): Double? {
        // Try each pattern in order
        for (pattern in totalPatterns.dropLast(1)) { // Skip the generic pattern first
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)
                    ?.replace(",", ".")
                    ?.replace(" ", "")
                amountStr?.toDoubleOrNull()?.let { return it }
            }
        }
        
        // Fallback: find the largest number that could be a total
        val genericPattern = totalPatterns.last()
        val matcher = genericPattern.matcher(text)
        val amounts = mutableListOf<Double>()
        
        while (matcher.find()) {
            val amountStr = matcher.group(1)
                ?.replace(",", ".")
                ?.replace(" ", "")
            amountStr?.toDoubleOrNull()?.let { amounts.add(it) }
        }
        
        // Return the largest amount (likely the total)
        return amounts.maxOrNull()
    }

    /**
     * Extract date from receipt
     */
    private fun extractDate(text: String): String? {
        for (pattern in datePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(0)
            }
        }
        return null
    }

    /**
     * Calculate confidence score for parsed data
     */
    fun getConfidenceScore(receipt: ParsedReceipt): Float {
        var score = 0f
        
        if (receipt.storeName != null && receipt.storeName.length >= 3) {
            score += 0.3f
        }
        if (receipt.totalAmount != null && receipt.totalAmount > 0) {
            score += 0.5f
        }
        if (receipt.date != null) {
            score += 0.2f
        }
        
        return score
    }
}
