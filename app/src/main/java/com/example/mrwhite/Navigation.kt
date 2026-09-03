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

import com.example.mrwhite.ui.screens.HowToPlayScreen
import com.example.mrwhite.ui.screens.AboutScreen
import com.example.mrwhite.ui.screens.FeedbackScreen

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
                onNext = { navController.navigate(Screen.Players.route) },
                onHowToPlayClick = { navController.navigate(Screen.HowToPlay.route) },
                onAboutClick = { navController.navigate(Screen.About.route) },
                onFeedbackClick = { navController.navigate(Screen.Feedback.route) }
            )
        }
        
        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }
        
        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        
        composable(Screen.Feedback.route) {
            FeedbackScreen(onBack = { navController.popBackStack() })
        }
        
        composable(Screen.Players.route) {
            PlayersScreen(
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() },
                onStart = {
                    if (gameViewModel.startGame()) {
                        navController.navigate(Screen.RoleReveal.route)
                    }
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
                onRestart = {
                    if (gameViewModel.restartGame()) {
                        navController.popBackStack(Screen.RoleReveal.route, inclusive = false)
                    }
                },
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
                onRestart = {
                    if (gameViewModel.restartGame()) {
                        navController.popBackStack(Screen.RoleReveal.route, inclusive = false)
                    }
                },
                onExit = {
                    navController.popBackStack(Screen.Setup.route, inclusive = false)
                }
            )
        }
    }
}
