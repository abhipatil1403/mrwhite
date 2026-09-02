package com.example.mrwhite.navigation

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Players : Screen("players")
    object RoleReveal : Screen("role_reveal")
    object Discussion : Screen("discussion")
    object Result : Screen("result")
    object HowToPlay : Screen("how_to_play")
    object About : Screen("about")
    object Feedback : Screen("feedback")
}
