package org.example.cardgame.controller

import jakarta.validation.Valid
import org.example.cardgame.error.*
import org.example.cardgame.service.AuthRequest
import org.example.cardgame.service.AuthResponse
import org.example.cardgame.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun registerUser(@RequestBody @Valid request: AuthRequest): ResponseEntity<Any> {
        return try {
            val response = authService.registerUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (ex: UserAlreadyExistsException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse("User with this login or username already exists")
            )
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(authService.authenticateUser(request))
        } catch (ex: InvalidCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (ex: UserNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}
