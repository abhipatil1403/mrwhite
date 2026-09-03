package com.example.mrwhite.data.model

data class PlayerAssignment(
    val player: Player,
    val role: Role,
    val word: String? // Mr. White has no word
)

data class GameState(
    val assignments: List<PlayerAssignment> = emptyList(),
    val revealedPlayers: Set<String> = emptySet(),
    val civilianWord: String? = null,
    val undercoverWord: String? = null,
    val discussionOrder: List<String> = emptyList(),
    val eliminatedPlayers: Set<String> = emptySet(),
    val winner: String? = null,
    val currentPhase: GamePhase = GamePhase.ROLE_REVEAL,
    val hasEliminatedThisRound: Boolean = false
)

enum class GamePhase {
    ROLE_REVEAL,
    DISCUSSION,
    ELIMINATION,
    RESULT
}
