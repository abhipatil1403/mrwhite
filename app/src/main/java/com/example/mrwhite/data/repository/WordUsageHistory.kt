package com.example.mrwhite.data.repository

import android.content.Context
import android.content.SharedPreferences

class WordUsageHistory(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mrwhite_word_history", Context.MODE_PRIVATE)

    var globalRoundNumber: Int
        get() = prefs.getInt("global_round_number", 0)
        set(value) = prefs.edit().putInt("global_round_number", value).apply()

    fun recordWordUsage(word: String, roundNumber: Int) {
        prefs.edit().putInt("word_${word.lowercase()}", roundNumber).apply()
    }

    fun recordPairUsage(pairKey: String, roundNumber: Int) {
        prefs.edit().putInt("pair_$pairKey", roundNumber).apply()
    }

    fun getLastUsedRoundForWord(word: String): Int {
        return prefs.getInt("word_${word.lowercase()}", -1)
    }

    fun getLastUsedRoundForPair(pairKey: String): Int {
        return prefs.getInt("pair_$pairKey", -1)
    }

    fun clearHistory() {
        prefs.edit().clear().apply()
    }
}
