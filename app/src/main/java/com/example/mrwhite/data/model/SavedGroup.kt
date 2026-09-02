package com.example.mrwhite.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SavedGroup(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val playerIds: Set<String>,
    val createdAt: Long = System.currentTimeMillis()
)
