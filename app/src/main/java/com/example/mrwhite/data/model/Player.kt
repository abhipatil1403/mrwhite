package com.example.mrwhite.data.model

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)
