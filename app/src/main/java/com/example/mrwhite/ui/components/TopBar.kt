package com.example.mrwhite.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.WhiteBackground

@Composable
fun TopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    showGameControls: Boolean = false,
    onRestartGame: (() -> Unit)? = null,
    onExitGame: (() -> Unit)? = null,
    onHowToPlayClick: (() -> Unit)? = null,
    onAboutClick: (() -> Unit)? = null,
    onFeedbackClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showMenuDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    val hasOverflowActions =
        onHowToPlayClick != null || onAboutClick != null || onFeedbackClick != null || onShareClick != null

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showGameControls) {
                IconButton(onClick = { showRestartDialog = true }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Restart", tint = BlackText)
                }
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(Icons.Filled.Close, contentDescription = "Exit", tint = BlackText)
                }
            } else if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BlackText)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BlackText,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        if (hasOverflowActions) {
            IconButton(onClick = { showMenuDialog = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = BlackText)
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }

    if (showMenuDialog && hasOverflowActions) {
        MenuDialog(
            onDismiss = { showMenuDialog = false },
            onHowToPlayClick = {
                showMenuDialog = false
                onHowToPlayClick?.invoke()
            },
            onAboutClick = {
                showMenuDialog = false
                onAboutClick?.invoke()
            },
            onFeedbackClick = {
                showMenuDialog = false
                onFeedbackClick?.invoke()
            },
            onShareClick = {
                showMenuDialog = false
                onShareClick?.invoke()
            }
        )
    }

    if (showRestartDialog) {
        ConfirmationDialog(
            text = "Are you sure you want to\nrestart the game?",
            onConfirm = {
                showRestartDialog = false
                onRestartGame?.invoke()
            },
            onDismiss = { showRestartDialog = false }
        )
    }

    if (showExitDialog) {
        ConfirmationDialog(
            text = "Are you sure you want to exit\nthe game?",
            onConfirm = {
                showExitDialog = false
                onExitGame?.invoke()
            },
            onDismiss = { showExitDialog = false }
        )
    }
}

@Composable
fun ConfirmationDialog(
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = WhiteBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlackText,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("No", color = BlackText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onConfirm) {
                        Text("Yes", color = BlackText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuDialog(
    onDismiss: () -> Unit,
    onHowToPlayClick: (() -> Unit)? = null,
    onAboutClick: (() -> Unit)? = null,
    onFeedbackClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = WhiteBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (onHowToPlayClick != null) {
                    MenuDialogItem("How to play?") { onHowToPlayClick() }
                }
                if (onAboutClick != null) {
                    MenuDialogItem("About the game") { onAboutClick() }
                }
                if (onShareClick != null) {
                    MenuDialogItem("Share with friends") { onShareClick() }
                }
                if (onFeedbackClick != null) {
                    MenuDialogItem("Send feedback") { onFeedbackClick() }
                }
            }
        }
    }
}

@Composable
private fun MenuDialogItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = BlackText,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    )
}
