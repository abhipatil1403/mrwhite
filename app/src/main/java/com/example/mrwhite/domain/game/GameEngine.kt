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

        return GameState(assignments = assignments)
    }
}
