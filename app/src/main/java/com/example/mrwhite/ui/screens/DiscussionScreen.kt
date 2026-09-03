package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
    onRestart: () -> Unit,
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Discussion",
            showGameControls = true,
            onRestartGame = onRestart,
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
                text = "Discuss and vote out undercovers!",
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
                // Active players in discussion order
                val activeDiscussionOrder = state.discussionOrder.filter { !state.eliminatedPlayers.contains(it) }
                items(activeDiscussionOrder, key = { it }) { playerId ->
                    val assignment = state.assignments.find { it.player.id == playerId } ?: return@items
                    val isSelected = selectedForElimination == playerId

                    DiscussionPlayerRow(
                        name = assignment.player.name,
                        role = assignment.role,
                        isEliminated = false,
                        isSelected = isSelected,
                        onClick = {
                            selectedForElimination = if (selectedForElimination == playerId) null else playerId
                        }
                    )
                }

                // Eliminated players at the bottom
                val eliminatedList = state.eliminatedPlayers.toList()
                if (eliminatedList.isNotEmpty()) {
                    items(eliminatedList, key = { it }) { playerId ->
                        val assignment = state.assignments.find { it.player.id == playerId } ?: return@items
                        
                        DiscussionPlayerRow(
                            name = assignment.player.name,
                            role = assignment.role,
                            isEliminated = true,
                            isSelected = false,
                            onClick = {}
                        )
                    }
                }
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (state.hasEliminatedThisRound) {
                    PrimaryButton(
                        text = "Another Discussion Round",
                        onClick = { 
                            viewModel.startNextDiscussionRound()
                            selectedForElimination = null
                        },
                        enabled = true
                    )
                } else {
                    PrimaryButton(
                        text = "▶ Eliminate",
                        onClick = { showEliminationDialog = true },
                        enabled = selectedForElimination != null
                    )
                }
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
        HorizontalDivider(color = GreyOutline, modifier = Modifier.padding(top = 16.dp))
    }
}
