package org.example.cardgame.dto

import org.example.cardgame.entity.Card
import org.example.cardgame.entity.GamePlayer
import org.example.cardgame.entity.GameSession

fun GameSession.toDTO(): GameSessionDTO = GameSessionDTO(
    id = id,
    status = status.toString(),
    players = players.sortedBy { it.joinOrder }.map { it.toPlayerDTO() },
    currentPlayerId = players.elementAtOrNull(currentPlayerIndex)?.user?.id,
    remainingCards = deck.size,
    maxPoints = maxPoints
)

fun GamePlayer.toPlayerDTO(): PlayerDTO = PlayerDTO(
    userId = user.id,
    username = user.username,
    points = points,
    isBlocked = isBlocked,
    joinOrder = joinOrder
)

fun Card.toDTO(): CardDTO = CardDTO(
    id = id,
    name = name,
    type = type.toString(),
    value = value
)