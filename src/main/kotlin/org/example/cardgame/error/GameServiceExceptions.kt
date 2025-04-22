package org.example.cardgame.error

import org.springframework.http.HttpStatus

class GameNotFoundException(gameId: Long) :
    GameException("Game with id $gameId not found", HttpStatus.NOT_FOUND)


class PlayerNotInGameException(userId: Long, gameId: Long) :
    GameException("User $userId is not participating in game $gameId")


class NotPlayerTurnException(userId: Long) :
    GameException("It's not user $userId turn to play")


class GameAlreadyStartedException(gameId: Long) :
    GameException("Game $gameId has already started")


class GameFullException(gameId: Long) :
    GameException("Game $gameId already has maximum players (4)")


class PlayerAlreadyInGameException(userId: Long, gameId: Long) :
    GameException("User $userId is already in game $gameId")


class NotEnoughPlayersException(gameId: Long) :
    GameException("Game $gameId requires at least 2 players to start")


class GameNotInProgressException(gameId: Long) :
    GameException("Game $gameId is not in progress")


class DeckEmptyException(gameId: Long) :
    GameException("Deck in game $gameId is empty")

class InvalidCardActionException(message: String) :
    GameException(message)

class PlayerNotFoundException(message: String) :
    GameException(message)

class UserAlreadyExistsException(message: String) :
    GameException(message)

class UserNotFoundException(message: String) :
    GameException(message)

class InvalidCredentialsException :
    GameException("Invalid login or password")