package org.example.cardgame.service

import org.example.cardgame.dto.GameSessionDTO
import org.example.cardgame.dto.TurnResultDTO
import org.example.cardgame.entity.*
import org.example.cardgame.repository.GameSessionRepository
import org.springframework.stereotype.Service
import org.example.cardgame.error.*
import org.example.cardgame.dto.*

@Service
class GameService (
    private val gameSessionRepository: GameSessionRepository,
    private val cardService: CardService
){
    fun createGameSession(user : User) : GameSessionDTO {
        val game = GameSession().apply {
            players.add(GamePlayer(user = user, gameSession = this, joinOrder = 1))
        }
        return gameSessionRepository.save(game).toDTO()
    }

    fun joinGame(gameId: Long, user : User) : GameSessionDTO {
        val game = gameSessionRepository.findById(gameId)
            .orElseThrow { GameNotFoundException(gameId) }

        if (game.status != GameStatus.WAITING_FOR_PLAYERS) {
            throw GameAlreadyStartedException(gameId)
        }

        if (game.players.size >= 4) {
            throw GameFullException(gameId)
        }

        if (game.players.any { it.user.id == user.id }) {
            throw PlayerAlreadyInGameException(user.id, gameId)
        }

        val nextJoinOrder = game.players.size + 1
        game.players.add(GamePlayer(
            user = user,
            gameSession = game,
            joinOrder = nextJoinOrder
        ))

        return gameSessionRepository.save(game).toDTO()
    }

    fun startGame(gameId: Long, user : User) : GameSessionDTO {
        val game = gameSessionRepository.findById(gameId)
            .orElseThrow { GameNotFoundException(gameId) }

        if (game.status != GameStatus.WAITING_FOR_PLAYERS) {
            throw GameAlreadyStartedException(gameId)
        }

        if (game.players.size < 2) {
            throw NotEnoughPlayersException(gameId)
        }

        if (!game.players.any { it.user.id == user.id }) {
            throw PlayerNotInGameException(user.id, gameId)
        }

        game.deck.addAll(cardService.generateShuffledDeck(game).map { card ->
            GameCard(card = card.card, gameSession = game)
        })

        game.status = GameStatus.IN_PROGRESS
        game.currentPlayerIndex = 0

        return gameSessionRepository.save(game).toDTO()
    }

    fun makeTurn(gameId: Long, user: User): TurnResultDTO {
        val game = gameSessionRepository.findById(gameId)
            .orElseThrow { GameNotFoundException(gameId) }

        if (game.status != GameStatus.IN_PROGRESS) {
            throw GameNotInProgressException(gameId)
        }

        val player = game.players.find { it.user.id == user.id }
            ?: throw PlayerNotInGameException(user.id, gameId)

        if (game.currentPlayerIndex != game.players.indexOf(player)) {
            throw NotPlayerTurnException(user.id)
        }

        if (game.deck.isEmpty()) {
            throw DeckEmptyException(gameId)
        }

        val card = game.deck.removeFirst()
        val result = cardService.applyCardEffect(card, player, game)

        val targetPlayer = result.targetPlayerId?.let { targetId ->
            game.players.find { it.user.id == targetId }
                ?: throw PlayerNotFoundException("Target player not found")
        }

        game.turns.add(Turn(
            gameSession = game,
            player = player,
            playedCard = card.card,
            pointsChange = result.pointsChange,
            targetPlayer = targetPlayer
        ))

        if (player.points >= game.maxPoints) {
            game.status = GameStatus.FINISHED
        } else {
            game.currentPlayerIndex = cardService.getNextPlayerIndex(game, card.card)
        }

        gameSessionRepository.save(game)

        return TurnResultDTO(
            card = card.card.toDTO(),
            pointsChange = result.pointsChange,
            currentPoints = player.points,
            nextPlayerId = game.players.elementAt(game.currentPlayerIndex).user.id
        )
    }

    fun getGameStatus(gameId: Long): GameStatusDTO {

        val game = gameSessionRepository.findById(gameId)
            .orElseThrow { GameNotFoundException(gameId) }

        return GameStatusDTO(
            gameId = game.id,
            status = game.status.name,
            players = game.players
                .sortedBy { it.joinOrder }
                .map { player ->
                    PlayerStatusDto(
                        userId = player.user.id,
                        username = player.user.username,
                        points = player.points,
                        isBlocked = player.isBlocked,
                        joinOrder = player.joinOrder
                    )
                },
            currentPlayerTurn = game.players
                .elementAtOrNull(game.currentPlayerIndex)?.user?.id,
            remainingCards = game.deck.size,
            maxPoints = game.maxPoints,
            winnerId = if (game.status == GameStatus.FINISHED) {
                game.players.maxByOrNull { it.points }?.user?.id
            } else null
        )
    }

    fun getActiveGames(includeFinished: Boolean): List<GameSessionDTO> {
        return gameSessionRepository.findAll()
            .filter { includeFinished || it.status != GameStatus.FINISHED }
            .map { it.toDTO() }
    }

}