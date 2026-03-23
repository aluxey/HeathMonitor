package com.cyberdoc.app.core

sealed interface AppError {
    val code: String
    val message: String
}

data class ValidationError(
    override val message: String,
    override val code: String = "validation_error",
) : AppError

data class NotFoundError(
    override val message: String,
    override val code: String = "not_found",
) : AppError

data class UnexpectedError(
    override val message: String,
    override val code: String = "unexpected_error",
) : AppError

data class IntegrationUnavailableError(
    override val message: String,
    override val code: String = "integration_unavailable",
) : AppError
