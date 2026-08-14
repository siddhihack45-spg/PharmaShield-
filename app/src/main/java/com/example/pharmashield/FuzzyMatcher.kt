package com.example.pharmashield

import kotlin.math.min

object FuzzyMatcher {

    /**
     * Calculates the Levenshtein Distance (edit distance) between two strings.
     */
    fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1) { 0 }

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1].equals(rhs[i - 1], ignoreCase = true)) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }

    /**
     * Checks if two strings are fuzzy matches based on maximum allowed edit distance.
     * @param maxDistance Maximum allowed edits (insertions, deletions, substitutions).
     */
    fun isFuzzyMatch(query: String, target: String, maxDistance: Int = 2): Boolean {
        val cleanQuery = query.trim().lowercase()
        val cleanTarget = target.trim().lowercase()

        // Direct containment check (e.g., OCR captured "Nimesulide Tab")
        if (cleanQuery.contains(cleanTarget) || cleanTarget.contains(cleanQuery)) {
            return true
        }

        // Levenshtein distance check for minor typos
        return levenshteinDistance(cleanQuery, cleanTarget) <= maxDistance
    }
}