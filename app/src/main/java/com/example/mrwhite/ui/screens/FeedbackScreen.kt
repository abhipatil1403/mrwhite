package com.example.mrwhite.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mrwhite.theme.BlackText
import com.example.mrwhite.theme.WhiteBackground
import com.example.mrwhite.ui.components.TopBar

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
            webView = null
        }
    }

    // Intercept back presses to handle WebView history
    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        TopBar(
            title = "Feedback",
            onBackClick = {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    onBack()
                }
            }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (hasError) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Could not load feedback form.", color = BlackText)
                    androidx.compose.material3.TextButton(onClick = {
                        hasError = false
                        isLoading = true
                        webView?.reload()
                    }) {
                        Text("Retry", color = BlackText)
                    }
                }
            } else {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true
                                    hasError = false
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }



                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    // Ignore errors for sub-resources if the main frame loads
                                    if (request?.isForMainFrame == true) {
                                        isLoading = false
                                        hasError = true
                                    }
                                }
                            }
                            loadUrl("https://forms.gle/iqYEs1tBcdchKxzL9")
                        }.also { webView = it }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading && !hasError) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BlackText
                )
            }
        }
    }
}
