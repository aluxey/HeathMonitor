package com.cyberdoc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.ui.CyberDocApp
import com.cyberdoc.app.ui.theme.CyberDocTheme

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
    }
}
