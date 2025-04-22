package org.example.cardgame.service

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.example.cardgame.config.JwtTokenProvider
import org.example.cardgame.entity.User
import org.example.cardgame.error.InvalidCredentialsException
import org.example.cardgame.error.UserAlreadyExistsException
import org.example.cardgame.error.UserNotFoundException
import org.example.cardgame.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun registerUser(request: AuthRequest): AuthResponse {

        if (userRepository.existsByLogin(request.login)) {
            throw UserAlreadyExistsException("Login ${request.login} already taken")
        }
        if (userRepository.existsByUsername(request.username)) {
            throw UserAlreadyExistsException("Username ${request.username} already taken")
        }

        val user = User(
            username = request.username,
            login = request.login,
            password = passwordEncoder.encode(request.password)
        ).also {
            userRepository.save(it)
        }

        val token = jwtTokenProvider.generateToken(user.login)

        return AuthResponse(
            token = token,
            userId = user.id
        )
    }

    fun authenticateUser(request: AuthRequest): AuthResponse {

        val user = userRepository.findByLogin(request.login)
            ?: throw UserNotFoundException("User with login ${request.login} not found")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidCredentialsException()
        }

        return AuthResponse(
            token = jwtTokenProvider.generateToken(user.login),
            userId = user.id
        )
    }

    fun getAuthenticatedUser(token: String): User {
        val login = jwtTokenProvider.getUsernameFromToken(token)
        return userRepository.findByLogin(login)
            ?: throw UserNotFoundException("User with login $login not found")
    }

}

data class AuthRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val login: String,
    @NotBlank
    @Size(min = 8, max = 100)
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long
)