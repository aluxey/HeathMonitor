package com.cyberdoc.app.app.di

import android.content.Context

object AppGraph {
    @Volatile
    private var _container: AppContainer? = null

    fun init(context: Context) {
        if (_container != null) return
        synchronized(this) {
            if (_container == null) {
                _container = DefaultAppContainer(context)
            }
        }
    }

    fun container(): AppContainer =
        checkNotNull(_container) { "AppGraph not initialized" }
}
