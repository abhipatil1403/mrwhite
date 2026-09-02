package com.example.mrwhite.domain.game

import com.example.mrwhite.data.model.*
import com.example.mrwhite.data.repository.WordRepository

class GameEngine(private val wordRepository: WordRepository = WordRepository()) {

    fun createGame(settings: GameSettings, players: List<Player>): GameState {
        val wordPair = wordRepository.getRandomWordPair()
        val normalWord = wordPair.first
        val undercoverWord = wordPair.second

        val roles = mutableListOf<Role>()
        repeat(settings.undercoverCount) { roles.add(Role.UNDERCOVER) }
        repeat(settings.mrWhiteCount) { roles.add(Role.MR_WHITE) }
        repeat(settings.totalPlayers - settings.undercoverCount - settings.mrWhiteCount) { roles.add(Role.NORMAL) }

        roles.shuffle()

        val assignments = players.mapIndexed { index, player ->
            val role = roles[index]
            val word = when (role) {
                Role.NORMAL -> normalWord
                Role.UNDERCOVER -> undercoverWord
                Role.MR_WHITE -> null
            }
            PlayerAssignment(player, role, word)
        }

        val discussionOrder = players.map { it.id }.shuffled()

        return GameState(
            assignments = assignments,
            civilianWord = normalWord,
            undercoverWord = undercoverWord,
            discussionOrder = discussionOrder
        )
    }

    fun evaluateGameState(state: GameState): GameState {
        val activeAssignments = state.assignments.filter { !state.eliminatedPlayers.contains(it.player.id) }
        
        val impostorsRemaining = activeAssignments.count { it.role == Role.UNDERCOVER || it.role == Role.MR_WHITE }
        val civiliansRemaining = activeAssignments.count { it.role == Role.NORMAL }

        return when {
            impostorsRemaining == 0 -> {
                // Civilians win
                state.copy(winner = "Civilians", currentPhase = GamePhase.RESULT)
            }
            civiliansRemaining <= impostorsRemaining -> {
                // Impostors win
                state.copy(winner = "Impostors", currentPhase = GamePhase.RESULT)
            }
            else -> {
                // Game continues, start next discussion round
                state.copy(
                    currentPhase = GamePhase.DISCUSSION,
                    clueCompletedPlayers = emptySet()
                )
            }
        }
    }
}
