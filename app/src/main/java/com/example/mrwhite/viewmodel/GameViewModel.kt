package com.example.mrwhite.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mrwhite.data.model.GameSettings
import com.example.mrwhite.data.model.Player
import com.example.mrwhite.data.repository.WordDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mrwhite.data.repository.WordRepository

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()
    private val _gameStartError = MutableStateFlow<String?>(null)
    val gameStartError: StateFlow<String?> = _gameStartError.asStateFlow()

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

        _settings.update { current ->
            sanitizeSettings(current.copy(selectedPlayerIds = current.selectedPlayerIds + newPlayer.id))
        }
        clearGameStartError()
    }

    fun deletePlayer(playerId: String) {
        _savedPlayers.value = playerRepository.deletePlayer(playerId)
        _settings.update { current ->
            sanitizeSettings(current.copy(selectedPlayerIds = current.selectedPlayerIds - playerId))
        }
        clearGameStartError()
    }

    fun togglePlayerSelection(playerId: String) {
        _settings.update { current ->
            val newIds = if (current.selectedPlayerIds.contains(playerId)) {
                current.selectedPlayerIds - playerId
            } else {
                current.selectedPlayerIds + playerId
            }
            sanitizeSettings(current.copy(selectedPlayerIds = newIds))
        }
        clearGameStartError()
    }

    fun toggleSelectAllPlayers() {
        _settings.update { current ->
            val allIds = _savedPlayers.value.map { it.id }.toSet()
            if (current.selectedPlayerIds.size == allIds.size && allIds.isNotEmpty()) {
                sanitizeSettings(current.copy(selectedPlayerIds = emptySet()))
            } else {
                sanitizeSettings(current.copy(selectedPlayerIds = allIds))
            }
        }
        clearGameStartError()
    }

    fun updateUndercoverCount(count: Int) {
        _settings.update { current ->
            sanitizeSettings(current.copy(undercoverCount = count.coerceAtLeast(0)))
        }
        clearGameStartError()
    }

    fun updateMrWhiteCount(count: Int) {
        _settings.update { current ->
            sanitizeSettings(current.copy(mrWhiteCount = count.coerceAtLeast(0)))
        }
        clearGameStartError()
    }

    private val groupRepository = com.example.mrwhite.data.repository.GroupRepository(application)
    private val _savedGroups = MutableStateFlow(groupRepository.getSavedGroups())
    val savedGroups: StateFlow<List<com.example.mrwhite.data.model.SavedGroup>> = _savedGroups.asStateFlow()

    fun createGroup(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || settings.value.selectedPlayerIds.isEmpty()) return
        
        _savedGroups.value = groupRepository.createGroup(trimmed, settings.value.selectedPlayerIds)
    }

    fun loadGroup(group: com.example.mrwhite.data.model.SavedGroup) {
        val availableIds = _savedPlayers.value.map { it.id }.toSet()
        _settings.update { current ->
            sanitizeSettings(current.copy(selectedPlayerIds = group.playerIds.intersect(availableIds)))
        }
        clearGameStartError()
    }

    fun deleteGroup(groupId: String) {
        _savedGroups.value = groupRepository.deleteGroup(groupId)
    }

    fun updateCategory(category: com.example.mrwhite.data.model.WordCategory) {
        val resolvedCategory =
            if (category == com.example.mrwhite.data.model.WordCategory.ANY || category in WordDatabase.selectableCategories) {
                category
            } else {
                com.example.mrwhite.data.model.WordCategory.ANY
            }
        _settings.value = _settings.value.copy(category = resolvedCategory)
        clearGameStartError()
    }

    private val wordRepository = WordRepository(application)
    private val gameEngine = com.example.mrwhite.domain.game.GameEngine(wordRepository)
    private val _gameState = MutableStateFlow<com.example.mrwhite.data.model.GameState?>(null)
    val gameState: StateFlow<com.example.mrwhite.data.model.GameState?> = _gameState.asStateFlow()

    fun startGame(): Boolean {
        val sanitizedSettings = sanitizeSettings(settings.value)
        if (sanitizedSettings != settings.value) {
            _settings.value = sanitizedSettings
        }

        val selectedPlayers = selectedPlayersFor(sanitizedSettings)
        if (!sanitizedSettings.isValid || selectedPlayers.size < 3) {
            _gameStartError.value =
                sanitizedSettings.validationMessage ?: "Select at least 3 players to start the game."
            return false
        }

        return runCatching {
            _gameState.value = gameEngine.createGame(sanitizedSettings, selectedPlayers)
            _gameStartError.value = null
            true
        }.getOrElse {
            _gameStartError.value =
                if (sanitizedSettings.category !in WordDatabase.selectableCategories &&
                    sanitizedSettings.category != com.example.mrwhite.data.model.WordCategory.ANY
                ) {
                    "That category is not available yet. Choose one of the listed categories."
                } else {
                    "Could not start the game. Please try again."
                }
            false
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



    fun eliminatePlayer(playerId: String) {
        _gameState.update { state ->
            if (state == null) return@update null
            val updatedState = state.copy(
                eliminatedPlayers = state.eliminatedPlayers + playerId,
                hasEliminatedThisRound = true
            )
            gameEngine.evaluateGameState(updatedState)
        }
    }

    fun startNextDiscussionRound() {
        _gameState.update { state ->
            if (state == null) return@update null
            
            val currentActive = state.discussionOrder.filter { !state.eliminatedPlayers.contains(it) }
            val shuffledActive = currentActive.shuffled().toMutableList()
            
            val newOrder = state.discussionOrder.map { playerId ->
                if (state.eliminatedPlayers.contains(playerId)) {
                    playerId 
                } else {
                    shuffledActive.removeAt(0) 
                }
            }

            state.copy(
                discussionOrder = newOrder,
                hasEliminatedThisRound = false
            )
        }
    }

    fun restartGame(): Boolean = startGame()

    fun exitGame() {
        _gameState.value = null
        _gameStartError.value = null
    }

    fun clearGameStartError() {
        _gameStartError.value = null
    }

    private fun selectedPlayersFor(settings: GameSettings): List<Player> =
        savedPlayers.value.filter { it.id in settings.selectedPlayerIds }

    private fun sanitizeSettings(settings: GameSettings): GameSettings {
        val availableIds = _savedPlayers.value.map { it.id }.toSet()
        val selectedIds = settings.selectedPlayerIds.intersect(availableIds)
        val maxSpecialRoles = (selectedIds.size - 1).coerceAtLeast(0)
        val safeMrWhiteCount = settings.mrWhiteCount.coerceAtLeast(0).coerceAtMost(maxSpecialRoles)
        val remainingSpecialSlots = (maxSpecialRoles - safeMrWhiteCount).coerceAtLeast(0)
        val safeUndercoverCount =
            settings.undercoverCount.coerceAtLeast(0).coerceAtMost(remainingSpecialSlots)
        val safeCategory =
            if (settings.category == com.example.mrwhite.data.model.WordCategory.ANY ||
                settings.category in WordDatabase.selectableCategories
            ) {
                settings.category
            } else {
                com.example.mrwhite.data.model.WordCategory.ANY
            }

        return settings.copy(
            selectedPlayerIds = selectedIds,
            undercoverCount = safeUndercoverCount,
            mrWhiteCount = safeMrWhiteCount,
            category = safeCategory
        )
    }
}
