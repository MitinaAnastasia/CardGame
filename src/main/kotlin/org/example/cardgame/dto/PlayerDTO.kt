package org.example.cardgame.dto

data class PlayerDTO (
    val userId: Long,
    val username: String,
    val points: Int,
    val isBlocked: Boolean,
    val joinOrder: Int
)