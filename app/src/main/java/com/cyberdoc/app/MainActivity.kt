package com.cyberdoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.cyberdoc.app.ui.CyberDocApp
import com.cyberdoc.app.ui.theme.CyberDocTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CyberDocApplication
        lifecycleScope.launch {
            app.container.seedDemoDataUseCase()
            app.container.syncHealthDataUseCase()
        }

        setContent {
            CyberDocTheme {
                CyberDocApp(container = app.container)
            }
        }
    }
}
