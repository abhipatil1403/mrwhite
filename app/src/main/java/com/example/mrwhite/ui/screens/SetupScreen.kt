package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.ErrorRed
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Saved Groups Section
            val groupsList by viewModel.savedGroups.collectAsState()
            if (groupsList.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text(
                        text = "Saved Groups",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groupsList.size) { index ->
                            val group = groupsList[index]
                            OutlinedButton(
                                onClick = { viewModel.loadGroup(group) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text(group.name)
                            }
                        }
                    }
                }
            }

            // Players Section
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                val playersList by viewModel.savedPlayers.collectAsState()
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Players (${settings.totalPlayers})",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                    if (playersList.isNotEmpty()) {
                        val allSelected = settings.selectedPlayerIds.size == playersList.size && playersList.isNotEmpty()
                        Text(
                            text = if (allSelected) "Deselect all" else "Select all",
                            fontSize = 14.sp,
                            color = BlackText.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { viewModel.toggleSelectAllPlayers() }.padding(4.dp)
                        )
                    }
                }

                if (playersList.isEmpty()) {
                    Text("No players added yet.", color = BlackText.copy(alpha = 0.6f), modifier = Modifier.padding(bottom = 8.dp))
                } else {
                    playersList.forEach { player ->
                        val isSelected = settings.selectedPlayerIds.contains(player.id)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.togglePlayerSelection(player.id) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = if (isSelected) "☑" else "☐",
                                fontSize = 24.sp,
                                color = BlackText,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = player.name,
                                fontSize = 18.sp,
                                color = BlackText
                            )
                        }
                    }
                }

                // Quick Add
                var newPlayerName by remember { mutableStateOf("") }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        placeholder = { Text("Enter player name") },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        singleLine = true,
                        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = BlackText,
                            unfocusedBorderColor = BlackText.copy(alpha = 0.3f)
                        )
                    )
                    OutlinedButton(
                        onClick = {
                            viewModel.addPlayer(newPlayerName)
                            newPlayerName = ""
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add")
                    }
                }

                // Save Group
                if (settings.selectedPlayerIds.isNotEmpty()) {
                    var newGroupName by remember { mutableStateOf("") }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = newGroupName,
                            onValueChange = { newGroupName = it },
                            placeholder = { Text("Group name") },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            singleLine = true,
                            colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = BlackText,
                                unfocusedBorderColor = BlackText.copy(alpha = 0.3f)
                            )
                        )
                        OutlinedButton(
                            onClick = {
                                viewModel.createGroup(newGroupName)
                                newGroupName = ""
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Group")
                        }
                    }
                }
            }

            NumberControl(
                label = "Undercovers",
                value = settings.undercoverCount,
                onDecrease = { viewModel.updateUndercoverCount(settings.undercoverCount - 1) },
                onIncrease = { viewModel.updateUndercoverCount(settings.undercoverCount + 1) }
            )

            NumberControl(
                label = "Mr White",
                value = settings.mrWhiteCount,
                onDecrease = { viewModel.updateMrWhiteCount(settings.mrWhiteCount - 1) },
                onIncrease = { viewModel.updateMrWhiteCount(settings.mrWhiteCount + 1) }
            )

            Column {
                Text(
                    text = "Category",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlackText)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(settings.category.displayName, fontSize = 18.sp)
                            Text("▼", fontSize = 14.sp)
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f).background(WhiteBackground)
                    ) {
                        com.example.mrwhite.data.model.WordCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName, color = BlackText, fontSize = 18.sp) },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Validation message
            settings.validationMessage?.let { message ->
                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PrimaryButton(
                    text = "Next",
                    onClick = onNext,
                    enabled = settings.isValid
                )
            }
        }
    }
}
