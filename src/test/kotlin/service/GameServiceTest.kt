package service

import org.example.cardgame.entity.*
import org.example.cardgame.repository.GameSessionRepository
import org.example.cardgame.service.CardEffectResult
import org.example.cardgame.service.CardService
import org.example.cardgame.service.GameService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class GameServiceTest {
    @InjectMocks
    lateinit var gameService: GameService

    @Mock
    lateinit var gameSessionRepository: GameSessionRepository
    @Mock
    lateinit var cardService: CardService


    private val user1 = User(1, "user1", "user1", "qwerty12345678")

    @Test
    fun createGameSessionTest() {
        whenever(gameSessionRepository.save(any())).thenAnswer { it.arguments[0] }

        val game = gameService.createGameSession(user1)

        assertEquals(1, game.players.size)
        assertEquals("user1", game.players[0].username)
    }

    @Test
    fun joinGameTest() {
        val game = GameSession().apply { players.add(GamePlayer(user = user1, joinOrder = 1))}
        whenever(gameSessionRepository.findById(any())).thenReturn(Optional.of(game))
        whenever(gameSessionRepository.save(any())).thenAnswer { it.arguments[0] }

        gameService.joinGame(1, User(2, "user2", "user2", "qwerty12345678"))

        assertEquals(2, game.players.size)
    }

    private fun testGameWithPlayers(): GameSession {
        return GameSession().apply {
            status = GameStatus.IN_PROGRESS
            currentPlayerIndex = 0
            maxPoints = 30
            players.add(GamePlayer(user = user1, joinOrder = 1, points = 28))
            players.add(GamePlayer(user = User(2, "user2", "user2", "qwerty12345678"), joinOrder = 2, points = 4))
            deck.add(
                GameCard(card = Card(id = 1L, type = CardType.POINTS, value = 5))
            )
        }
    }

    @Test
    fun makeTurnWinTest() {
        val game = testGameWithPlayers()

        whenever(gameSessionRepository.findById(any())).thenReturn(Optional.of(game))
        whenever(cardService.applyCardEffect(any(), any(), any()))
            .thenAnswer { value ->
                val (card, player, _) = value.arguments
                (player as GamePlayer).points += (card as GameCard).card.value
                CardEffectResult((card).card.value, null)
            }
        whenever(gameSessionRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = gameService.makeTurn(1, user1)

        assertEquals(33, result.currentPoints)
        assertEquals(GameStatus.FINISHED, game.status)
    }

    @Test
    fun makeTurnBlockTest() {
        val game = testGameWithPlayers().apply {
            deck.clear()
            deck.add(GameCard(card = Card(id = 1L, name = "Block", type = CardType.ACTION, value = 1)))
            players.add(GamePlayer(
                user = User(3, "user3", "user3", "qwerty12345678"),
                joinOrder = 3,
                points = 10
            ))
        }

        whenever(gameSessionRepository.findById(any())).thenReturn(Optional.of(game))
        whenever(cardService.applyCardEffect(any(), any(), any()))
            .thenAnswer { _ ->
                CardEffectResult(
                    pointsChange = 0,
                    targetPlayerId = null
                )
            }
        whenever(cardService.getNextPlayerIndex(any(), any())).thenReturn(2)
        whenever(gameSessionRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = gameService.makeTurn(1, user1)

        assertEquals(0, result.pointsChange)
        assertEquals(28, result.currentPoints)
        assertEquals(2, game.currentPlayerIndex)
        assertEquals(3L, result.nextPlayerId)
    }
}