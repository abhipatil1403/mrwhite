package com.example.mrwhite.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.mrwhite.data.model.SavedGroup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GroupRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mrwhite_groups", Context.MODE_PRIVATE)

    fun getSavedGroups(): List<SavedGroup> {
        val json = prefs.getString("groups_list", "[]") ?: "[]"
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveGroups(groups: List<SavedGroup>) {
        val json = Json.encodeToString(groups)
        prefs.edit().putString("groups_list", json).apply()
    }
    
    fun createGroup(name: String, playerIds: Set<String>): List<SavedGroup> {
        val current = getSavedGroups().toMutableList()
        // If a group with the same name exists, we could replace or skip. We'll skip or allow dupes.
        // Let's just create it.
        val newGroup = SavedGroup(name = name, playerIds = playerIds)
        current.add(newGroup)
        current.sortByDescending { it.createdAt }
        saveGroups(current)
        return current
    }

    fun deleteGroup(groupId: String): List<SavedGroup> {
        val current = getSavedGroups().filter { it.id != groupId }
        saveGroups(current)
        return current
    }
}
