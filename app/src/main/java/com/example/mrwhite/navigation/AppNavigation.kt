package com.example.mrwhite.navigation

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Players : Screen("players")
    object RoleReveal : Screen("role_reveal")
    object Game : Screen("game")
    object Voting : Screen("voting")
    object Result : Screen("result")
}
