package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.data.model.Role
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.GreyOutline
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.ConfirmationDialog
import com.example.mrwhite.ui.components.PrimaryButton
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun DiscussionScreen(
    viewModel: GameViewModel,
    onExit: () -> Unit,
    onResult: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val state = gameState ?: return

    var selectedForElimination by remember { mutableStateOf<String?>(null) }
    var showEliminationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentPhase) {
        if (state.currentPhase == com.example.mrwhite.data.model.GamePhase.RESULT) {
            onResult()
        }
    }

    val activePlayers = state.assignments.filter { !state.eliminatedPlayers.contains(it.player.id) }
    val allCluesGiven = state.clueCompletedPlayers.size >= activePlayers.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Discussion",
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
                text = "Each player says a word related to their word one-by-one.\nAfter all players are done, discuss and vote out undercovers!",
                fontSize = 16.sp,
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
                items(state.discussionOrder, key = { it }) { playerId ->
                    val assignment = state.assignments.find { it.player.id == playerId } ?: return@items
                    val isEliminated = state.eliminatedPlayers.contains(playerId)
                    val isClueGiven = state.clueCompletedPlayers.contains(playerId)
                    val isSelected = selectedForElimination == playerId

                    DiscussionPlayerRow(
                        name = assignment.player.name,
                        role = assignment.role,
                        isEliminated = isEliminated,
                        isClueGiven = isClueGiven,
                        isSelected = isSelected,
                        onClick = {
                            if (isEliminated) return@DiscussionPlayerRow
                            
                            if (!allCluesGiven) {
                                viewModel.markClueCompleted(playerId)
                            } else {
                                selectedForElimination = if (selectedForElimination == playerId) null else playerId
                            }
                        }
                    )
                }
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PrimaryButton(
                    text = "▶ Eliminate",
                    onClick = { showEliminationDialog = true },
                    enabled = allCluesGiven && selectedForElimination != null
                )
            }
        }
    }

    if (showEliminationDialog && selectedForElimination != null) {
        val selectedName = state.assignments.find { it.player.id == selectedForElimination }?.player?.name ?: ""
        ConfirmationDialog(
            text = "Eliminate $selectedName?",
            onConfirm = {
                viewModel.eliminatePlayer(selectedForElimination!!)
                selectedForElimination = null
                showEliminationDialog = false
            },
            onDismiss = { showEliminationDialog = false }
        )
    }
}

@Composable
fun DiscussionPlayerRow(
    name: String,
    role: Role,
    isEliminated: Boolean,
    isClueGiven: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEliminated, onClick = onClick)
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "Selected",
                        tint = BlackText,
                        modifier = Modifier.padding(end = 8.dp).size(20.dp)
                    )
                } else if (isClueGiven && !isEliminated) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Done",
                        tint = BlackText,
                        modifier = Modifier.padding(end = 8.dp).size(20.dp)
                    )
                }
                
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isEliminated) BlackText.copy(alpha = 0.4f) else BlackText,
                    textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None
                )
            }
            
            if (isEliminated) {
                Text(
                    text = when (role) {
                        Role.NORMAL -> "[ CIV ]"
                        Role.UNDERCOVER -> "[ UNDERCOVER ]"
                        Role.MR_WHITE -> "[ MR WHITE ]"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText.copy(alpha = 0.6f)
                )
            }
        }
        Divider(color = GreyOutline, modifier = Modifier.padding(top = 16.dp))
    }
}
