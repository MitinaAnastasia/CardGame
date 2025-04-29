package org.example.cardgame.dto

data class GameStatusDTO(
    val gameId: Long,
    val status: String,
    val players: List<PlayerStatusDto>,
    val currentPlayerTurn: Long?,
    val remainingCards: Int,
    val maxPoints: Int,
    val winnerId: Long?
)

data class PlayerStatusDto(
    val userId: Long,
    val username: String,
    val points: Int,
    val isBlocked: Boolean,
    val joinOrder: Int
)