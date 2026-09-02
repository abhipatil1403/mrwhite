package com.example.mrwhite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mrwhite.data.model.GameSettings
import com.example.mrwhite.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val _players = MutableStateFlow(List(4) { index -> Player(name = "Player ${index + 1}") })
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    fun updateTotalPlayers(count: Int) {
        if (count in 3..20) {
            _settings.update { it.copy(totalPlayers = count) }
            adjustPlayersList(count)
        }
    }

    private fun adjustPlayersList(newCount: Int) {
        _players.update { currentList ->
            when {
                newCount > currentList.size -> {
                    val additionalPlayers = List(newCount - currentList.size) { index ->
                        Player(name = "Player ${currentList.size + index + 1}")
                    }
                    currentList + additionalPlayers
                }
                newCount < currentList.size -> {
                    currentList.take(newCount)
                }
                else -> currentList
            }
        }
    }

    fun updatePlayerName(playerId: String, newName: String) {
        _players.update { currentList ->
            currentList.map {
                if (it.id == playerId) it.copy(name = newName) else it
            }
        }
    }

    fun updateUndercoverCount(count: Int) {
        if (count >= 0) {
            _settings.update { it.copy(undercoverCount = count) }
        }
    }

    fun updateMrWhiteCount(count: Int) {
        if (count >= 0) {
            _settings.update { it.copy(mrWhiteCount = count) }
        }
    }

    private val gameEngine = com.example.mrwhite.domain.game.GameEngine()
    private val _gameState = MutableStateFlow<com.example.mrwhite.data.model.GameState?>(null)
    val gameState: StateFlow<com.example.mrwhite.data.model.GameState?> = _gameState.asStateFlow()

    fun startGame() {
        if (settings.value.isValid) {
            _gameState.value = gameEngine.createGame(settings.value, players.value)
        }
    }

    fun markPlayerRevealed(playerId: String) {
        _gameState.update { state ->
            if (state == null) return@update null
            val updatedRevealed = state.revealedPlayers + playerId
            state.copy(revealedPlayers = updatedRevealed)
        }
    }

    fun proceedToDiscussion() {
        _gameState.update { state ->
            state?.copy(currentPhase = com.example.mrwhite.data.model.GamePhase.DISCUSSION)
        }
    }

    fun proceedToElimination() {
        _gameState.update { state ->
            state?.copy(currentPhase = com.example.mrwhite.data.model.GamePhase.ELIMINATION)
        }
    }

    fun markClueCompleted(playerId: String) {
        _gameState.update { state ->
            if (state == null) return@update null
            state.copy(clueCompletedPlayers = state.clueCompletedPlayers + playerId)
        }
    }

    fun eliminatePlayer(playerId: String) {
        _gameState.update { state ->
            if (state == null) return@update null
            val updatedState = state.copy(eliminatedPlayers = state.eliminatedPlayers + playerId)
            gameEngine.evaluateGameState(updatedState)
        }
    }

    fun restartGame() {
        if (settings.value.isValid) {
            _gameState.value = gameEngine.createGame(settings.value, players.value)
        }
    }

    fun exitGame() {
        _gameState.value = null
    }
}
