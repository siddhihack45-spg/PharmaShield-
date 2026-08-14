package com.example.pharmashield

import org.junit.Assert.assertEquals
import org.junit.Test

class OcrParserTest {

    @Test
    fun testParseStandardFormat() {
        val result = OcrParser.parsePrescriptionText("Paracetamol 500mg")
        assertEquals("Paracetamol", result.drugName)
        assertEquals("500mg", result.dosage)
    }

    @Test
    fun testParseWithNewlineAndCapsules() {
        val result = OcrParser.parsePrescriptionText("Amoxicillin\n250 mg capsules")
        assertEquals("Amoxicillin", result.drugName)
        assertEquals("250 mg", result.dosage)
    }

    @Test
    fun testNoDosageFound() {
        val result = OcrParser.parsePrescriptionText("Aspirin")
        assertEquals("Aspirin", result.drugName)
        assertEquals("N/A", result.dosage)
    }

    @Test
    fun testNoisyTextExtraction() {
        val result = OcrParser.parsePrescriptionText("!!! Ibuprofen ### 400 mg")
        assertEquals("Ibuprofen", result.drugName)
        assertEquals("400 mg", result.dosage)
    }

    @Test
    fun testDecimalDosageExtraction() {
        val result = OcrParser.parsePrescriptionText("Prednisolone 2.5 mg")
        assertEquals("Prednisolone", result.drugName)
        assertEquals("2.5 mg", result.dosage)
    }
}