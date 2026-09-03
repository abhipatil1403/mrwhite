package com.example.mrwhite.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.data.model.Player
import com.example.mrwhite.data.repository.WordDatabase
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.ErrorRed
import com.example.mrwhite.theme.GreyOutline
import com.example.mrwhite.theme.NavyPrimary
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.NumberControl
import com.example.mrwhite.ui.components.PrimaryButton
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun SetupScreen(
    viewModel: GameViewModel,
    onNext: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onAboutClick: () -> Unit,
    onFeedbackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val playersList by viewModel.savedPlayers.collectAsState()
    val groupsList by viewModel.savedGroups.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Setup",
            onHowToPlayClick = onHowToPlayClick,
            onAboutClick = onAboutClick,
            onFeedbackClick = onFeedbackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SetupSummaryCard(
                playerCount = settings.totalPlayers,
                undercoverCount = settings.undercoverCount,
                mrWhiteCount = settings.mrWhiteCount,
                validationMessage = settings.validationMessage
            )

            if (groupsList.isNotEmpty()) {
                SectionCard(title = "Saved groups", subtitle = "Reuse a party setup in one tap.") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(groupsList, key = { it.id }) { group ->
                            OutlinedButton(
                                onClick = { viewModel.loadGroup(group) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(group.name)
                            }
                        }
                    }
                }
            }

            SectionCard(
                title = "Players",
                subtitle = if (playersList.isEmpty()) "Add your group first." else "Select who is joining this round."
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${settings.totalPlayers} selected",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BlackText
                    )
                    if (playersList.isNotEmpty()) {
                        val allSelected =
                            settings.selectedPlayerIds.size == playersList.size && playersList.isNotEmpty()
                        Text(
                            text = if (allSelected) "Deselect all" else "Select all",
                            fontSize = 14.sp,
                            color = NavyPrimary,
                            modifier = Modifier
                                .clickable { viewModel.toggleSelectAllPlayers() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (playersList.isEmpty()) {
                    Text(
                        text = "No players added yet.",
                        fontSize = 15.sp,
                        color = BlackText.copy(alpha = 0.65f)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        playersList.forEach { player ->
                            PlayerSelectionRow(
                                player = player,
                                selected = player.id in settings.selectedPlayerIds,
                                onToggle = { viewModel.togglePlayerSelection(player.id) }
                            )
                        }
                    }
                }

                var newPlayerName by remember { mutableStateOf("") }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        placeholder = { Text("Enter player name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyPrimary,
                            focusedLabelColor = NavyPrimary,
                            unfocusedBorderColor = GreyOutline
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            viewModel.addPlayer(newPlayerName)
                            newPlayerName = ""
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text("Add")
                    }
                }

                if (settings.selectedPlayerIds.isNotEmpty()) {
                    var newGroupName by remember { mutableStateOf("") }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newGroupName,
                            onValueChange = { newGroupName = it },
                            placeholder = { Text("Save this group as...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyPrimary,
                                focusedLabelColor = NavyPrimary,
                                unfocusedBorderColor = GreyOutline
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.createGroup(newGroupName)
                                newGroupName = ""
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }

            SectionCard(title = "Roles", subtitle = "Keep special roles balanced for the selected players.") {
                NumberControl(
                    label = "Undercovers",
                    value = settings.undercoverCount,
                    onDecrease = { viewModel.updateUndercoverCount(settings.undercoverCount - 1) },
                    onIncrease = { viewModel.updateUndercoverCount(settings.undercoverCount + 1) }
                )
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = GreyOutline)
                Spacer(modifier = Modifier.height(20.dp))
                NumberControl(
                    label = "Mr White",
                    value = settings.mrWhiteCount,
                    onDecrease = { viewModel.updateMrWhiteCount(settings.mrWhiteCount - 1) },
                    onIncrease = { viewModel.updateMrWhiteCount(settings.mrWhiteCount + 1) }
                )
            }

            SectionCard(title = "Category", subtitle = "Only categories with live word packs are shown.") {
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(settings.category.displayName, fontSize = 17.sp)
                            Text("Change", color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(WhiteBackground)
                    ) {
                        WordDatabase.selectableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName, color = BlackText, fontSize = 17.sp) },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.padding(20.dp)) {
            PrimaryButton(
                text = "Continue",
                onClick = onNext,
                enabled = settings.isValid
            )
        }
    }
}

@Composable
private fun SetupSummaryCard(
    playerCount: Int,
    undercoverCount: Int,
    mrWhiteCount: Int,
    validationMessage: String?
) {
    SectionCard(
        title = "Round overview",
        subtitle = "Build a valid group before moving to the reveal flow."
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryPill(label = "$playerCount players")
            SummaryPill(label = "$undercoverCount undercover")
            SummaryPill(label = "$mrWhiteCount Mr White")
        }

        if (validationMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = validationMessage,
                color = ErrorRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PlayerSelectionRow(
    player: Player,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) NavyPrimary.copy(alpha = 0.08f) else WhiteBackground,
        tonalElevation = if (selected) 2.dp else 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) NavyPrimary.copy(alpha = 0.35f) else GreyOutline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = player.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = BlackText
            )
            Text(
                text = if (selected) "Selected" else "Tap to add",
                fontSize = 13.sp,
                color = if (selected) NavyPrimary else BlackText.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = WhiteBackground,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, GreyOutline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BlackText
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = BlackText.copy(alpha = 0.65f)
            )
            content()
        }
    }
}

@Composable
private fun SummaryPill(label: String) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Medium,
                color = BlackText
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = NavyPrimary.copy(alpha = 0.08f),
            disabledLabelColor = BlackText
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = false,
            borderColor = NavyPrimary.copy(alpha = 0.2f),
            disabledBorderColor = NavyPrimary.copy(alpha = 0.2f)
        )
    )
}
