package com.example.mrwhite.domain.game

import com.example.mrwhite.data.model.GameSettings
import com.example.mrwhite.data.model.Player
import com.example.mrwhite.data.model.Role
import com.example.mrwhite.data.model.WordCategory
import com.example.mrwhite.data.model.WordPairData
import com.example.mrwhite.data.repository.WordRepository
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GameEngineRandomizationTest {

    @Test
    fun testRoleAssignmentIsRandomizedIndependently() {
        val mockRepo = mock(WordRepository::class.java)
        `when`(mockRepo.getRandomWordPair(WordCategory.ANY)).thenReturn(
            WordPairData("id_any", WordCategory.ANY, "Civ", "Und")
        )

        val engine = GameEngine(mockRepo)
        val players = listOf(
            Player(name = "A"),
            Player(name = "B"),
            Player(name = "C"),
            Player(name = "D"),
            Player(name = "E"),
            Player(name = "F")
        )
        val settings = GameSettings(
            selectedPlayerIds = players.map { it.id }.toSet(),
            undercoverCount = 1,
            mrWhiteCount = 1
        )

        var playerA_CivilianCount = 0
        var playerA_UndercoverCount = 0
        var playerA_MrWhiteCount = 0

        for (i in 0 until 1000) {
            val state = engine.createGame(settings, players)
            val roleA = state.assignments.find { it.player.name == "A" }?.role
            when (roleA) {
                Role.NORMAL -> playerA_CivilianCount++
                Role.UNDERCOVER -> playerA_UndercoverCount++
                Role.MR_WHITE -> playerA_MrWhiteCount++
                null -> {}
            }
        }

        // With 6 players, probabilities:
        // Undercover: 1/6 (~166 times)
        // Mr. White: 1/6 (~166 times)
        // Civilian: 4/6 (~666 times)
        // We assert that Player A gets every role at least some number of times, proving it's randomized and not deterministically bound to position 0.
        assertTrue("Player A was never Civilian", playerA_CivilianCount > 50)
        assertTrue("Player A was never Undercover", playerA_UndercoverCount > 20)
        assertTrue("Player A was never Mr. White", playerA_MrWhiteCount > 20)
    }
}
