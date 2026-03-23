package com.cyberdoc.app.core

sealed class AppResult<out T> {
    data class Success<T>(val value: T) : AppResult<T>()
    data class Failure(val error: AppError) : AppResult<Nothing>()
}

inline fun <T> appResult(block: () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (t: Throwable) {
        AppResult.Failure(UnexpectedError(t.message ?: "Unexpected runtime error"))
    }
