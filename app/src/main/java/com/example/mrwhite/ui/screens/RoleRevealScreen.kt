package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mrwhite.data.model.PlayerAssignment
import com.example.mrwhite.data.model.Role
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.GreyOutline
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.theme.NavyPrimary
import com.example.mrwhite.ui.components.PrimaryButton
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.ui.components.ConfirmationDialog
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun RoleRevealScreen(
    viewModel: GameViewModel,
    onGamePhaseStart: () -> Unit,
    onExit: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val state = gameState ?: return

    var selectedAssignment by remember { mutableStateOf<PlayerAssignment?>(null) }
    var assignmentToConfirmReReveal by remember { mutableStateOf<PlayerAssignment?>(null) }

    // Listen for phase change to navigate
    LaunchedEffect(state.currentPhase) {
        if (state.currentPhase == com.example.mrwhite.data.model.GamePhase.DISCUSSION) {
            onGamePhaseStart()
        }
    }

    val allRevealed = state.revealedPlayers.size == state.assignments.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Reveal",
            showGameControls = true,
            onRestartGame = { viewModel.restartGame() },
            onExitGame = {
                viewModel.exitGame()
                onExit()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Each player should tap on their name to see their secret word!",
                fontSize = 18.sp,
                color = BlackText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.assignments, key = { it.player.id }) { assignment ->
                    val isRevealed = state.revealedPlayers.contains(assignment.player.id)
                    PlayerRevealRow(
                        assignment = assignment,
                        isRevealed = isRevealed,
                        onClick = {
                            if (!isRevealed) {
                                selectedAssignment = assignment
                            } else {
                                assignmentToConfirmReReveal = assignment
                            }
                        }
                    )
                }
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PrimaryButton(
                    text = "▶ Go to discussion",
                    onClick = { viewModel.proceedToDiscussion() },
                    enabled = allRevealed
                )
            }
        }
    }

    selectedAssignment?.let { assignment ->
        SecretRevealDialog(
            assignment = assignment,
            onDismiss = {
                viewModel.markPlayerRevealed(assignment.player.id)
                selectedAssignment = null
            }
        )
    }

    assignmentToConfirmReReveal?.let { assignment ->
        ConfirmationDialog(
            text = "Are you sure you want to reveal\n${assignment.player.name}'s role again?",
            onConfirm = {
                assignmentToConfirmReReveal = null
                selectedAssignment = assignment
            },
            onDismiss = { assignmentToConfirmReReveal = null }
        )
    }
}

@Composable
fun PlayerRevealRow(
    assignment: PlayerAssignment,
    isRevealed: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = assignment.player.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = if (isRevealed) BlackText.copy(alpha = 0.4f) else BlackText
            )
            
            if (isRevealed) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Seen",
                    tint = BlackText.copy(alpha = 0.4f)
                )
            }
        }
        HorizontalDivider(color = GreyOutline, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun SecretRevealDialog(
    assignment: PlayerAssignment,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {}) { // Disable outside touch to ensure they hit OK
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = WhiteBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = assignment.player.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Text(
                    text = when (assignment.role) {
                        Role.NORMAL -> "Your word is -"
                        Role.UNDERCOVER -> "You are -"
                        Role.MR_WHITE -> "You are -"
                    },
                    fontSize = 18.sp,
                    color = BlackText,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Surface(
                    color = NavyPrimary,
                    shape = androidx.compose.ui.graphics.RectangleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (assignment.role) {
                            Role.NORMAL -> assignment.word ?: ""
                            Role.UNDERCOVER -> "Undercover!" // Abstract undercover mechanic
                            Role.MR_WHITE -> "Mr. White!"
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = androidx.compose.ui.graphics.Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                }

                PrimaryButton(
                    text = "OK",
                    onClick = onDismiss,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}
