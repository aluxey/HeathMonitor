package com.cyberdoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.ui.CyberDocApp
import com.cyberdoc.app.ui.theme.CyberDocTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppGraph.init(applicationContext)

        setContent {
            CyberDocTheme {
                CyberDocApp()
            }
        }

        lifecycleScope.launch {
            // Laisse le premier rendu se stabiliser avant le warmup backend.
            delay(600)
            runCatching {
                withContext(Dispatchers.IO) {
                    val container = AppGraph.container()
                    container.bootstrapMvpDataUseCase()
                    container.syncHealthConnectDataUseCase(daysBack = 7)
                }
            }
        }
    }
}
