package com.example.mrwhite.domain.game

import com.example.mrwhite.data.model.WordCategory
import com.example.mrwhite.data.model.WordPairData
import com.example.mrwhite.data.repository.WordUsageHistory
import kotlin.random.Random

class WordSelectionEngine(
    private val history: WordUsageHistory,
    private val allPairs: List<WordPairData>
) {
    companion object {
        const val HARD_COOLDOWN_ROUNDS = 30
    }

    fun selectWordPair(category: WordCategory): WordPairData {
        // Step 1: Filter by category
        val candidatePool = if (category == WordCategory.ANY) {
            allPairs
        } else {
            allPairs.filter { it.category == category }
        }

        if (candidatePool.isEmpty()) {
            throw IllegalStateException("No words available for category $category")
        }

        val currentRound = history.globalRoundNumber

        // Evaluate candidates
        val evaluatedCandidates = candidatePool.map { pair ->
            val lastUsedCiv = history.getLastUsedRoundForWord(pair.civilianWord)
            val lastUsedUnd = history.getLastUsedRoundForWord(pair.undercoverWord)
            val lastUsedPair = history.getLastUsedRoundForPair(pair.pairKey)

            val roundsSinceCiv = if (lastUsedCiv == -1) Int.MAX_VALUE else currentRound - lastUsedCiv
            val roundsSinceUnd = if (lastUsedUnd == -1) Int.MAX_VALUE else currentRound - lastUsedUnd
            val roundsSincePair = if (lastUsedPair == -1) Int.MAX_VALUE else currentRound - lastUsedPair

            val minRoundsSinceAnyUsage = minOf(roundsSinceCiv, roundsSinceUnd, roundsSincePair)

            Pair(pair, minRoundsSinceAnyUsage)
        }

        // Step 2 & 3: Try to find candidates outside hard cooldown
        var validCandidates = evaluatedCandidates.filter { it.second > HARD_COOLDOWN_ROUNDS }

        // Step 15: Failsafe when pool is small
        if (validCandidates.isEmpty()) {
            // Relax hard cooldown, just pick the oldest used ones
            val oldestUsage = evaluatedCandidates.maxOfOrNull { it.second } ?: 0
            validCandidates = evaluatedCandidates.filter { it.second == oldestUsage }
        }

        // Step 5 & 6: Assign weights and select
        val weightedCandidates = validCandidates.map { (pair, roundsSince) ->
            val weight = when {
                roundsSince == Int.MAX_VALUE -> 1.0 // Never used
                roundsSince > 100 -> 0.90
                roundsSince > 60 -> 0.60
                roundsSince > 30 -> 0.35
                else -> 0.10 // Failsafe fallback weight
            }
            Pair(pair, weight)
        }

        val selectedPair = selectWeightedRandom(weightedCandidates)

        // Step 9: Persist
        history.globalRoundNumber = currentRound + 1
        history.recordWordUsage(selectedPair.civilianWord, currentRound)
        history.recordWordUsage(selectedPair.undercoverWord, currentRound)
        history.recordPairUsage(selectedPair.pairKey, currentRound)

        return selectedPair
    }

    private fun selectWeightedRandom(candidates: List<Pair<WordPairData, Double>>): WordPairData {
        val totalWeight = candidates.sumOf { it.second }
        var randomValue = Random.nextDouble() * totalWeight

        for ((pair, weight) in candidates) {
            randomValue -= weight
            if (randomValue <= 0.0) {
                return pair
            }
        }
        return candidates.last().first
    }
}
