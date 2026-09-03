package com.example.mrwhite.domain.game

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.mrwhite.data.model.WordCategory
import com.example.mrwhite.data.model.WordPairData
import com.example.mrwhite.data.repository.WordUsageHistory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WordSelectionEngineTest {

    private lateinit var history: WordUsageHistory
    private lateinit var engine: WordSelectionEngine
    private val mockPairs = listOf(
        WordPairData("id_1", WordCategory.ANIMALS, "Cat", "Dog"),
        WordPairData("id_2", WordCategory.ANIMALS, "Bird", "Fish"),
        WordPairData("id_3", WordCategory.FOOD, "Apple", "Banana"),
        WordPairData("id_4", WordCategory.FOOD, "Pizza", "Burger")
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        history = WordUsageHistory(context)
        history.clearHistory()
        engine = WordSelectionEngine(history, mockPairs)
    }

    @Test
    fun testWordIsSelectedSuccessfully() {
        val pair = engine.selectWordPair(WordCategory.ANY)
        assertNotNull(pair)
    }

    @Test
    fun testCategoryFiltering() {
        val pair = engine.selectWordPair(WordCategory.FOOD)
        assertEquals(WordCategory.FOOD, pair.category)
    }

    @Test
    fun testUnsupportedCategoryFallsBackToAvailablePool() {
        val pair = engine.selectWordPair(WordCategory.VEHICLES)
        assertTrue(mockPairs.contains(pair))
    }

    @Test
    fun testHardCooldownPreventsImmediateReuse() {
        val firstPair = engine.selectWordPair(WordCategory.FOOD)
        // Only 2 pairs in FOOD, so the second call must return the other one
        val secondPair = engine.selectWordPair(WordCategory.FOOD)
        
        assertNotEquals(firstPair.pairKey, secondPair.pairKey)
    }
}
