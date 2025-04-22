package org.example.cardgame.service

import org.example.cardgame.entity.*
import org.example.cardgame.error.InvalidCardActionException
import org.example.cardgame.repository.CardRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.asKotlinRandom


@Service
class CardService (
    private val cardRepository: CardRepository
) {
    private val random = Random()

    fun generateShuffledDeck(gameSession: GameSession): List<GameCard> {
        val baseCards = cardRepository.findAll()

        val deck = mutableListOf<Card>()
        repeat(4) { deck.addAll(baseCards) }

        return deck.shuffled(random).mapIndexed { index, card ->
            GameCard(
                card = card,
                gameSession = gameSession,
                positionInDeck = index
            )
        }
    }

    fun applyCardEffect(card: GameCard, currentPlayer: GamePlayer, game: GameSession): CardEffectResult {
        return when {
            card.card.type == CardType.POINTS -> applyPointsEffect(card.card, currentPlayer)
            card.card.name == "Block" -> applyBlockEffect()
            card.card.name.startsWith("Steal") -> applyStealEffect(card.card, currentPlayer, game)
            card.card.name == "DoubleDown" -> applyDoubleDownEffect(currentPlayer, game)
            else -> throw InvalidCardActionException("Unknown card type: ${card.card.name}")
        }.also {
            card.isUsed = true
        }
    }

    private fun applyPointsEffect(card: Card, player: GamePlayer): CardEffectResult {
        player.points += card.value
        return CardEffectResult(card.value, null)
    }

    private fun applyBlockEffect(): CardEffectResult {
        return CardEffectResult(0, null)
    }

    private fun applyStealEffect(card: Card, currentPlayer: GamePlayer, game: GameSession): CardEffectResult {
        val otherPlayers = game.players.filter { it != currentPlayer && !it.isBlocked }
        if (otherPlayers.isEmpty()) return CardEffectResult(0, null)

        val target = otherPlayers.random(random.asKotlinRandom())
        val stealAmount = minOf(card.value, target.points)

        target.points -= stealAmount
        currentPlayer.points += stealAmount

        return CardEffectResult(stealAmount, target.user.id)
    }

    private fun applyDoubleDownEffect(player: GamePlayer, game: GameSession): CardEffectResult {
        val newPoints = minOf(player.points * 2, game.maxPoints)
        val pointsGained = newPoints - player.points
        player.points = newPoints
        return CardEffectResult(pointsGained, null)
    }

    fun getNextPlayerIndex(game: GameSession, playedCard: Card): Int {
        val currentIndex = game.currentPlayerIndex

        return if (playedCard.name == "Block") {
            findNextUnblockedPlayer(game, currentIndex, skip = 2)
        } else {
            findNextUnblockedPlayer(game, currentIndex, skip = 1)
        }
    }

    private fun findNextUnblockedPlayer(game: GameSession, currentIndex: Int, skip: Int): Int {
        val players = game.players.sortedBy { it.joinOrder }
        var nextIndex = (currentIndex + skip) % players.size
        var attempts = 0

        while (players[nextIndex].isBlocked && attempts < players.size) {
            nextIndex = (nextIndex + 1) % players.size
            attempts++
        }

        return players[nextIndex].let { game.players.indexOf(it) }
    }

}

data class CardEffectResult(
    val pointsChange: Int,
    val targetPlayerId: Long?
)