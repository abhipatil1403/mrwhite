package com.example.mrwhite.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.GreyOutline
import com.example.mrwhite.theme.NavyPrimary
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.TopBar
import com.example.mrwhite.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    viewModel: GameViewModel,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val state = gameState ?: return
    val context = LocalContext.current

    val impostorsWon = state.winner == "Impostors"
    
    val undercoverWord = state.undercoverWord ?: "???"
    val civilianWord = state.civilianWord ?: "???"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Result",
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${state.winner} won!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BlackText,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Words card
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .border(2.dp, GreyOutline, RoundedCornerShape(16.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Undercover word
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = undercoverWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F) // Red
                    )
                }

                HorizontalDivider(color = GreyOutline, thickness = 2.dp)

                // Civilian word
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = civilianWord,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C) // Green
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (impostorsWon) {
                Text(
                    text = "Loving it?",
                    fontSize = 16.sp,
                    color = BlackText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TextButton(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Play Mr. White with your friends! It's an awesome social deduction game.")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                ) {
                    Text(
                        text = "Share with friends →",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Re-using TopBar's restart/exit functionality but from bottom buttons if needed,
            // or just text buttons as per spec
            TextButton(onClick = onRestart) {
                Text(
                    text = "Restart",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText
                )
            }
            TextButton(
                onClick = {
                    viewModel.exitGame()
                    onExit()
                }
            ) {
                Text(
                    text = "Exit",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlackText
                )
            }
        }
    }
}
