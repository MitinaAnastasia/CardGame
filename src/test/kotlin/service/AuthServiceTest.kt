package service

import org.example.cardgame.config.JwtTokenProvider
import org.example.cardgame.repository.UserRepository
import org.example.cardgame.service.AuthRequest
import org.example.cardgame.service.AuthService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.security.crypto.password.PasswordEncoder
import org.example.cardgame.entity.User
import org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @InjectMocks
    lateinit var authService: AuthService

    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var passwordEncoder: PasswordEncoder
    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    private val testRequest = AuthRequest("test user", "test user", "password_qwerty")

    @Test
    fun registerUserTest() {
        whenever(userRepository.existsByLogin(any())).thenReturn(false)
        whenever(passwordEncoder.encode(any())).thenReturn("encoded")
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(jwtTokenProvider.generateToken(any())).thenReturn("test-token")

        val result = authService.registerUser(testRequest)

        verify(passwordEncoder).encode("password_qwerty")
        assertEquals("test-token", result.token)
    }

    @Test
    fun loginUserTest() {
        val user = User(1, "test user", "test user", "encoded")
        whenever(userRepository.findByLogin("test user")).thenReturn(user)
        whenever(passwordEncoder.matches(any(), any())).thenReturn(true)
        whenever(jwtTokenProvider.generateToken(any())).thenReturn("test-token")

        val result = authService.authenticateUser(testRequest)

        assertEquals("test-token", result.token)
    }
}