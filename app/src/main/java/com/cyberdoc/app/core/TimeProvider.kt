package com.cyberdoc.app.core

import java.time.Instant

fun interface TimeProvider {
    fun now(): Instant
}

object SystemTimeProvider : TimeProvider {
    override fun now(): Instant = Instant.now()
}
