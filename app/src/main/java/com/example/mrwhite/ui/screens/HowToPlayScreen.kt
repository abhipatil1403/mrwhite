package com.example.mrwhite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.TopBar

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "How to Play",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Section(
                title = "The Idea",
                body = "Mr. White is a local pass-the-phone social deduction game. Everyone gets a secret word, but one or more players might get a different word (Undercover) or no word at all (Mr. White). Your goal is to figure out who is who by describing your word."
            )

            Section(
                title = "Roles",
                body = "• Civilian: Gets the normal secret word.\n• Undercover: Gets a similar, but slightly different secret word.\n• Mr. White: Gets no word at all!"
            )

            Section(
                title = "Your Mission",
                body = "• Civilians: Find the Undercovers and Mr. White.\n• Undercovers: Blend in, survive, and eliminate the Civilians.\n• Mr. White: Blend in, survive, and figure out the Civilians' secret word."
            )

            Section(
                title = "How a Round Works",
                body = "1. Reveal: Pass the phone around so everyone can secretly view their role and word.\n2. Clue: Each player says one single word related to their secret word.\n3. Discuss: After everyone has given a clue, discuss who might be the impostors.\n4. Vote: Vote to eliminate the most suspicious player.\n5. Continue: If the game isn't over, give another round of clues.\n6. Win: Civilians win by eliminating all impostors. Impostors win if they survive until there's only one Civilian left."
            )

            Section(
                title = "Important",
                body = "Do NOT say your secret word directly! If your clue is too obvious, Mr. White will easily guess your word and win. If your clue is too vague, the Civilians might mistake you for an impostor and vote you out."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Section(title: String, body: String) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BlackText,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = body,
            fontSize = 16.sp,
            color = BlackText,
            lineHeight = 24.sp
        )
    }
}
