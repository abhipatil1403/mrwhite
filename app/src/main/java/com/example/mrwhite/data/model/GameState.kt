package com.example.mrwhite.data.model

data class PlayerAssignment(
    val player: Player,
    val role: Role,
    val word: String? // Mr. White has no word
)

data class GameState(
    val assignments: List<PlayerAssignment> = emptyList(),
    val revealedPlayers: Set<String> = emptySet(),
    val currentPhase: GamePhase = GamePhase.ROLE_REVEAL
)

enum class GamePhase {
    ROLE_REVEAL,
    GAME,
    VOTING,
    RESULT
}
