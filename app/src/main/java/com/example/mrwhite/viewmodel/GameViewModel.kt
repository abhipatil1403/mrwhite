package com.example.mrwhite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mrwhite.data.model.GameSettings
import com.example.mrwhite.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mrwhite.data.repository.WordRepository

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    private val playerRepository = com.example.mrwhite.data.repository.PlayerRepository(application)
    
    private val _savedPlayers = MutableStateFlow(playerRepository.getSavedPlayers())
    val savedPlayers: StateFlow<List<Player>> = _savedPlayers.asStateFlow()

    fun addPlayer(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        
        // Case-insensitive duplicate check
        if (_savedPlayers.value.any { it.name.equals(trimmed, ignoreCase = true) }) {
            // Already exists, could set an error state here, but for simplicity just return
            return
        }
        
        val newPlayer = Player(name = trimmed)
        _savedPlayers.value = playerRepository.addPlayer(newPlayer)
        
        // Auto-select the new player
        togglePlayerSelection(newPlayer.id)
    }

    fun togglePlayerSelection(playerId: String) {
        _settings.update { current ->
            val newIds = if (current.selectedPlayerIds.contains(playerId)) {
                current.selectedPlayerIds - playerId
            } else {
                current.selectedPlayerIds + playerId
            }
            current.copy(selectedPlayerIds = newIds)
        }
    }

    fun updateUndercoverCount(count: Int) {
        if (count >= 0) {
            _settings.update { it.copy(undercoverCount = count) }
        }
    }

    fun updateMrWhiteCount(count: Int) {
        _settings.value = _settings.value.copy(mrWhiteCount = count)
    }

    fun updateCategory(category: com.example.mrwhite.data.model.WordCategory) {
        _settings.value = _settings.value.copy(category = category)
    }

    private val wordRepository = WordRepository(application)
    private val gameEngine = com.example.mrwhite.domain.game.GameEngine(wordRepository)
    private val _gameState = MutableStateFlow<com.example.mrwhite.data.model.GameState?>(null)
    val gameState: StateFlow<com.example.mrwhite.data.model.GameState?> = _gameState.asStateFlow()

    fun startGame() {
        if (settings.value.isValid) {
            val selectedPlayers = savedPlayers.value.filter { settings.value.selectedPlayerIds.contains(it.id) }
            _gameState.value = gameEngine.createGame(settings.value, selectedPlayers)
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
            val selectedPlayers = savedPlayers.value.filter { settings.value.selectedPlayerIds.contains(it.id) }
            _gameState.value = gameEngine.createGame(settings.value, selectedPlayers)
        }
    }

    fun exitGame() {
        _gameState.value = null
    }
}
