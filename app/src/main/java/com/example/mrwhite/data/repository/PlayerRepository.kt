package com.example.mrwhite.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.mrwhite.data.model.Player
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlayerRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mrwhite_players", Context.MODE_PRIVATE)

    fun getSavedPlayers(): List<Player> {
        val json = prefs.getString("players_list", "[]") ?: "[]"
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun savePlayers(players: List<Player>) {
        val json = Json.encodeToString(players)
        prefs.edit().putString("players_list", json).apply()
    }
    
    fun addPlayer(player: Player): List<Player> {
        val current = getSavedPlayers().toMutableList()
        current.add(player)
        // Sort alphabetically case-insensitive
        current.sortBy { it.name.lowercase() }
        savePlayers(current)
        return current
    }
}
