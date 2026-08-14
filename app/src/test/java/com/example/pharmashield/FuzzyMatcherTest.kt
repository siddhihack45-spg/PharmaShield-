package com.example.pharmashield

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FuzzyMatcherTest {

    @Test
    fun levenshteinDistance_exactMatch_returnsZero() {
        val distance = FuzzyMatcher.levenshteinDistance("Nimesulide", "Nimesulide")
        assertEquals(0, distance)
    }

    @Test
    fun levenshteinDistance_caseInsensitive_returnsZero() {
        val distance = FuzzyMatcher.levenshteinDistance("NIMESULIDE", "nimesulide")
        assertEquals(0, distance)
    }

    @Test
    fun levenshteinDistance_singleCharTypo_returnsOne() {
        val distance = FuzzyMatcher.levenshteinDistance("Nimesulid", "Nimesulide")
        assertEquals(1, distance)
    }

    @Test
    fun levenshteinDistance_twoCharTypo_returnsTwo() {
        val distance = FuzzyMatcher.levenshteinDistance("Nimeslid", "Nimesulide")
        assertEquals(2, distance)
    }

    @Test
    fun isFuzzyMatch_minorTypoWithinMaxDistance_returnsTrue() {
        val result = FuzzyMatcher.isFuzzyMatch("Nimesulid", "Nimesulide", maxDistance = 2)
        assertTrue(result)
    }

    @Test
    fun isFuzzyMatch_exactMatch_returnsTrue() {
        val result = FuzzyMatcher.isFuzzyMatch("Analgin", "Analgin", maxDistance = 2)
        assertTrue(result)
    }

    @Test
    fun isFuzzyMatch_substringContainment_returnsTrue() {
        val result = FuzzyMatcher.isFuzzyMatch("Nimesulide 500mg", "Nimesulide", maxDistance = 2)
        assertTrue(result)
    }

    @Test
    fun isFuzzyMatch_exceedsMaxDistance_returnsFalse() {
        val result = FuzzyMatcher.isFuzzyMatch("Amoxicillin", "Nimesulide", maxDistance = 2)
        assertFalse(result)
    }
}
