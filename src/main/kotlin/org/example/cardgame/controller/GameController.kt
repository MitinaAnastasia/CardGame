package org.example.cardgame.controller

import org.example.cardgame.dto.GameSessionDTO
import org.example.cardgame.dto.GameStatusDTO
import org.example.cardgame.error.*
import org.example.cardgame.service.AuthService
import org.example.cardgame.service.GameService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/games")
class GameController (
    private val gameService: GameService,
    private val authService: AuthService
){
    @PostMapping
    fun createGame(
        @RequestHeader("Authorization") token: String,
        @RequestParam(required = false) maxPoints: Int?
    ): ResponseEntity<GameSessionDTO> {
        return try {
            val user = authService.getAuthenticatedUser(token)
            val game = gameService.createGameSession(user)
            ResponseEntity.ok(game)
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/{gameId}/join")
    fun joinGame(
        @RequestHeader("Authorization") token: String,
        @PathVariable gameId: Long
    ): ResponseEntity<Any> {
        return try {
            val user = authService.getAuthenticatedUser(token)
            ResponseEntity.ok(gameService.joinGame(gameId, user))
        } catch (ex: GameNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (ex: GameFullException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse("Game is full (max 4 players)")
            )
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/{gameId}/start")
    fun startGame(
        @RequestHeader("Authorization") token: String,
        @PathVariable gameId: Long
    ): ResponseEntity<Any> {
        return try {
            val user = authService.getAuthenticatedUser(token)
            ResponseEntity.ok(gameService.startGame(gameId, user))
        } catch (ex: NotEnoughPlayersException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse("Need at least 2 players to start")
            )
        } catch (ex: GameAlreadyStartedException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse("Game already started")
            )
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/{gameId}/turns")
    fun makeTurn(
        @RequestHeader("Authorization") token: String,
        @PathVariable gameId: Long
    ): ResponseEntity<Any> {
        return try {
            val user = authService.getAuthenticatedUser(token)
            ResponseEntity.ok(gameService.makeTurn(gameId, user))
        } catch (ex: NotPlayerTurnException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponse("Not your turn")
            )
        } catch (ex: DeckEmptyException) {
            ResponseEntity.status(HttpStatus.GONE).body(
                ErrorResponse("Deck is empty")
            )
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/{gameId}/status")
    fun getGameStatus(
        @PathVariable gameId: Long
    ): ResponseEntity<GameStatusDTO> {
        return try {
            ResponseEntity.ok(gameService.getGameStatus(gameId))
        } catch (ex: GameNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping
    fun getActiveGames(
        @RequestParam(defaultValue = "false") includeFinished: Boolean
    ): ResponseEntity<List<GameSessionDTO>> {
        return try {
            ResponseEntity.ok(gameService.getActiveGames(includeFinished))
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }
}