package org.example.cardgame.dto

data class TurnResultDTO (
    val card: CardDTO,
    val pointsChange: Int,
    val currentPoints: Int,
    val nextPlayerId: Long
)