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
