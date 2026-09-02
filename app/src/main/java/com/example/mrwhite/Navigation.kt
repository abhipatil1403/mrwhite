package com.example.mrwhite

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mrwhite.navigation.Screen
import com.example.mrwhite.ui.screens.PlayersScreen
import com.example.mrwhite.ui.screens.RoleRevealScreen
import com.example.mrwhite.ui.screens.SetupScreen
import com.example.mrwhite.viewmodel.GameViewModel

import com.example.mrwhite.ui.screens.DiscussionScreen
import com.example.mrwhite.ui.screens.ResultScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Setup.route,
        modifier = Modifier.safeDrawingPadding()
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                viewModel = gameViewModel,
                onNext = { navController.navigate(Screen.Players.route) }
            )
        }
        
        composable(Screen.Players.route) {
            PlayersScreen(
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() },
                onStart = {
                    gameViewModel.startGame()
                    navController.navigate(Screen.RoleReveal.route)
                }
            )
        }
        
        composable(Screen.RoleReveal.route) {
            RoleRevealScreen(
                viewModel = gameViewModel,
                onGamePhaseStart = {
                    navController.navigate(Screen.Discussion.route)
                },
                onExit = {
                    navController.popBackStack(Screen.Setup.route, inclusive = false)
                }
            )
        }
        
        composable(Screen.Discussion.route) {
            DiscussionScreen(
                viewModel = gameViewModel,
                onExit = {
                    navController.popBackStack(Screen.Setup.route, inclusive = false)
                },
                onResult = {
                    navController.navigate(Screen.Result.route)
                }
            )
        }
        
        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = gameViewModel,
                onExit = {
                    navController.popBackStack(Screen.Setup.route, inclusive = false)
                }
            )
        }
    }
}
