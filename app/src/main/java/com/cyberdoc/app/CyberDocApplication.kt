package com.cyberdoc.app

import android.app.Application
import com.cyberdoc.app.app.AppContainer
import com.cyberdoc.app.app.DefaultAppContainer

class CyberDocApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
