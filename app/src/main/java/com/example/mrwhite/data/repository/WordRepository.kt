package com.example.mrwhite.data.repository

import android.content.Context
import com.example.mrwhite.data.model.WordCategory
import com.example.mrwhite.data.model.WordPairData
import com.example.mrwhite.domain.game.WordSelectionEngine

class WordRepository(context: Context) {
    private val history = WordUsageHistory(context)
    private val engine = WordSelectionEngine(history, WordDatabase.allPairs)

    fun getRandomWordPair(category: WordCategory): WordPairData {
        return engine.selectWordPair(category)
    }
}
