package com.example.mrwhite.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.TopBar

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionName = remember { getVersionName(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "About",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MR. WHITE",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BlackText,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Version $versionName",
                fontSize = 16.sp,
                color = BlackText.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Text(
                text = "A modern local pass-the-phone social deduction party game.",
                fontSize = 18.sp,
                color = BlackText,
                textAlign = TextAlign.Center,
                lineHeight = 26.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            Text(
                text = "Created by",
                fontSize = 14.sp,
                color = BlackText.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "Abhishek Patil",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BlackText
            )
        }
    }
}

private fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}
