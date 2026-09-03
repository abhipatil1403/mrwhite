package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.ErrorRed
import com.example.mrwhite.theme.GreyOutline
import com.example.mrwhite.theme.NavyPrimary
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.PrimaryButton
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun PlayersScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    val players by viewModel.savedPlayers.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val gameStartError by viewModel.gameStartError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Players",
            onBackClick = onBack
        )

        val selectedPlayers = players.filter { settings.selectedPlayerIds.contains(it.id) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(22.dp),
                color = NavyPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyPrimary.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ready to start",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackText
                    )
                    Text(
                        text = "${selectedPlayers.size} players selected • ${settings.undercoverCount} undercover • ${settings.mrWhiteCount} Mr White",
                        fontSize = 15.sp,
                        color = BlackText.copy(alpha = 0.7f)
                    )
                }
            }

            if (gameStartError != null) {
                Text(
                    text = gameStartError!!,
                    color = ErrorRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedPlayers.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = WhiteBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreyOutline)
                        ) {
                            Text(
                                text = "No players are selected. Go back and choose at least three players.",
                                color = BlackText.copy(alpha = 0.7f),
                                fontSize = 15.sp,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }
                } else {
                    items(selectedPlayers, key = { it.id }) { player ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.dp, GreyOutline),
                            shape = RoundedCornerShape(18.dp),
                            color = WhiteBackground,
                            shadowElevation = 4.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreyOutline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = BlackText
                                )
                                Text(
                                    text = "In round",
                                    fontSize = 13.sp,
                                    color = NavyPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        PaddingValues(20.dp).let { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PrimaryButton(
                    text = "Start game",
                    onClick = onStart,
                    enabled = settings.isValid
                )
            }
        }
    }
}
