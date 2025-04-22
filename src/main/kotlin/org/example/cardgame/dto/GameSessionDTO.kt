package org.example.cardgame.dto

data class GameSessionDTO (
    val id: Long,
    val status: String,
    val players: List<PlayerDTO>,
    val currentPlayerId: Long?,
    val remainingCards: Int,
    val maxPoints: Int
)