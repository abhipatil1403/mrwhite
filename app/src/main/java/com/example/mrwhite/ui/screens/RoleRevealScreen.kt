package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.data.model.Role
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.PrimaryButton
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun RoleRevealScreen(
    viewModel: GameViewModel,
    onGamePhaseStart: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    var isRevealed by remember { mutableStateOf(false) }

    val state = gameState ?: return
    val currentAssignment = state.assignments[state.currentPlayerIndex]

    // Listen for phase change to navigate
    LaunchedEffect(state.currentPhase) {
        if (state.currentPhase == com.example.mrwhite.data.model.GamePhase.GAME) {
            onGamePhaseStart()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(title = "Reveal Role")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isRevealed) {
                Text(
                    text = "Pass the phone to",
                    fontSize = 20.sp,
                    color = BlackText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = currentAssignment.player.name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = when (currentAssignment.role) {
                        Role.NORMAL -> "YOUR WORD"
                        Role.UNDERCOVER -> "YOU ARE UNDERCOVER"
                        Role.MR_WHITE -> "YOU ARE MR. WHITE"
                    },
                    fontSize = 20.sp,
                    color = BlackText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (currentAssignment.word != null) {
                    Text(
                        text = currentAssignment.word,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "MR WHITE",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (!isRevealed) {
                    PrimaryButton(
                        text = "Reveal",
                        onClick = { isRevealed = true }
                    )
                } else {
                    PrimaryButton(
                        text = "Hide & Continue",
                        onClick = { 
                            isRevealed = false
                            viewModel.nextPlayerReveal()
                        }
                    )
                }
            }
        }
    }
}
