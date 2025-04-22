package org.example.cardgame.error

data class ErrorResponse(
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)