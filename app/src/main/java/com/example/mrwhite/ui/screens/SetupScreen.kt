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
    onNext: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(title = "Setup")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            NumberControl(
                label = "Total players",
                value = settings.totalPlayers,
                onDecrease = { viewModel.updateTotalPlayers(settings.totalPlayers - 1) },
                onIncrease = { viewModel.updateTotalPlayers(settings.totalPlayers + 1) }
            )

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
