package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mrwhite.theme.NavyPrimary
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.PrimaryButton
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    val players by viewModel.players.collectAsState()
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Players",
            onBackClick = onBack
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(players, key = { it.id }) { player ->
                OutlinedTextField(
                    value = player.name,
                    onValueChange = { viewModel.updatePlayerName(player.id, it) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = NavyPrimary,
                        cursorColor = NavyPrimary
                    ),
                    singleLine = true
                )
            }
        }

        PaddingValues(24.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PrimaryButton(
                    text = "Start",
                    onClick = onStart,
                    enabled = settings.isValid
                )
            }
        }
    }
}
