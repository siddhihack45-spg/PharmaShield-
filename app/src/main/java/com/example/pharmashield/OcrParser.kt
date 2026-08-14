package com.example.pharmashield

data class ParsedPrescription(val drugName: String, val dosage: String)

object OcrParser {

    fun parsePrescriptionText(rawText: String): ParsedPrescription {
        val dosageRegex = Regex("""(?i)\b(\d+(?:\.\d+)?\s*(?:mg|ml|g|mcg|i\.u\.|iu))\b""")
        // Matches common dosage form keywords to filter out noise
        val formKeywordsRegex = Regex("""(?i)\b(capsules|capsule|tablets|tablet|pills|pill|syrup|solution|mg|ml|g|mcg|rx)\b""")

        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        var detectedDosage = ""
        var dosageLineIndex = -1

        // 1. Scan for dosage pattern across all lines
        for ((index, line) in lines.withIndex()) {
            val match = dosageRegex.find(line)
            if (match != null) {
                detectedDosage = match.value
                dosageLineIndex = index
                break
            }
        }

        // 2. Determine drug name
        var detectedName = ""
        if (dosageLineIndex != -1) {
            // If dosage is on a lower line (index > 0), the drug name is on the preceding line
            if (dosageLineIndex > 0 && lines[dosageLineIndex - 1].isNotBlank()) {
                detectedName = lines[dosageLineIndex - 1]
            } else {
                // Same line dosage: remove dosage and form keywords
                detectedName = lines[dosageLineIndex]
                    .replace(dosageRegex, "")
                    .replace(formKeywordsRegex, "")
                    .trim()
            }
        }

        // Fallback: Use first non-empty line
        if (detectedName.isBlank() && lines.isNotEmpty()) {
            detectedName = lines.first()
        }

        // Clean extra non-alphanumeric noise symbols
        detectedName = detectedName
            .replace(formKeywordsRegex, "")
            .replace(Regex("""[^a-zA-Z0-9\s]"""), "")
            .trim()

        return ParsedPrescription(
            drugName = detectedName.ifEmpty { "Unknown" },
            dosage = detectedDosage.ifEmpty { "N/A" }
        )
    }
}