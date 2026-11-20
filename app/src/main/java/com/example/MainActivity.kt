package com.example

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DangaBhasaTheme
import com.example.ui.theme.MidnightRiver

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
    )
    val initialName = intent?.data?.getQueryParameter("name").orEmpty()
    setContent {
      DangaBhasaTheme {
        DangaBhasaScreen(initialName = initialName)
      }
    }
  }
}

private class DangaWebBridge(private val context: Context) {
  @JavascriptInterface
  fun shareWhatsApp(message: String, shareUrl: String) {
    val whatsappPackages = listOf("com.whatsapp", "com.whatsapp.w4b")
    for (pkg in whatsappPackages) {
      val waIntent =
        Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          setPackage(pkg)
          putExtra(Intent.EXTRA_TEXT, message)
        }
      if (waIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(waIntent)
        return
      }
    }

    val chooser =
      Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_SUBJECT, "ଶୁଭ ଡଙ୍ଗା ଭସା ଓ ବୋଇତ ବନ୍ଦାଣ")
          putExtra(Intent.EXTRA_TEXT, message)
        },
        "WhatsApp / Share via",
      )
    context.startActivity(chooser)
  }

  @JavascriptInterface
  fun copyLink(url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Danga Bhasa Greeting Link", url))
    Toast.makeText(context, "ଶୁଭେଚ୍ଛା ଲିଙ୍କ୍ କପି ହୋଇଗଲା!", Toast.LENGTH_SHORT).show()
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DangaBhasaScreen(initialName: String) {
  val context = LocalContext.current
  var isLoading by remember { mutableStateOf(true) }
  var canGoBack by remember { mutableStateOf(false) }
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  val htmlContent =
    remember(context) {
      context.assets.open("danga_bhasa.html").bufferedReader().use { it.readText() }
    }

  val baseUrl =
    remember(initialName) {
      val root = "https://ais-pre-ueuynacyvdn3t5b3swkwvd-100214857189.asia-east1.run.app/"
      if (initialName.isNotBlank()) {
        "$root?name=${Uri.encode(initialName)}"
      } else {
        root
      }
    }

  BackHandler(enabled = canGoBack) {
    webViewRef?.goBack()
  }

  Box(
    modifier =
      Modifier
        .fillMaxSize()
        .background(MidnightRiver)
        .windowInsetsPadding(WindowInsets.safeDrawing),
  ) {
    AndroidView(
      modifier = Modifier.fillMaxSize().testTag("danga_bhasa_webview"),
      factory = { ctx ->
        WebView(ctx).apply {
          setBackgroundColor(android.graphics.Color.parseColor("#050E23"))
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.useWideViewPort = true
          settings.loadWithOverviewMode = true
          settings.cacheMode = WebSettings.LOAD_DEFAULT
          settings.builtInZoomControls = false
          settings.displayZoomControls = false
          addJavascriptInterface(DangaWebBridge(ctx), "AndroidBridge")
          webViewClient =
            object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                isLoading = false
                canGoBack = view?.canGoBack() == true
              }

              override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
              ): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme.orEmpty()
                val host = uri.host.orEmpty()
                if (scheme == "whatsapp" || host == "wa.me" || host == "api.whatsapp.com") {
                  val externalIntent = Intent(Intent.ACTION_VIEW, uri)
                  runCatching { ctx.startActivity(externalIntent) }
                  return true
                }
                return false
              }
            }
          loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null)
          webViewRef = this
        }
      },
      update = { webView ->
        webViewRef = webView
      },
    )

    AnimatedVisibility(
      visible = isLoading,
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center),
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = stringResource(id = R.string.app_name),
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}
