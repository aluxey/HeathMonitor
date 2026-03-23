package com.cyberdoc.app.ui.figma

import android.content.Context

class FigmaSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isOnboardingDone(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(done: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    companion object {
        private const val PREF_NAME = "figma_session"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}
