package org.example.cardgame.error

import org.springframework.http.HttpStatus

abstract class GameException(
    message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)