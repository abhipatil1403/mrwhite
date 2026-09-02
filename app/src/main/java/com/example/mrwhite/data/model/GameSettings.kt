package com.example.mrwhite.data.model

data class GameSettings(
    val totalPlayers: Int = 4,
    val undercoverCount: Int = 1,
    val mrWhiteCount: Int = 0,
    val category: WordCategory = WordCategory.ANY
) {
    val isValid: Boolean
        get() = totalPlayers >= 3 &&
                (undercoverCount + mrWhiteCount > 0) &&
                (undercoverCount + mrWhiteCount < totalPlayers)

    val validationMessage: String?
        get() = when {
            totalPlayers < 3 -> "At least 3 players are required."
            undercoverCount + mrWhiteCount == 0 -> "Add at least one Undercover or Mr White."
            undercoverCount + mrWhiteCount >= totalPlayers -> "Too many special roles for the number of players."
            else -> null
        }
}
