package org.example.cardgame.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class JwtTokenProvider (
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
){
    fun generateToken(login: String): String {
        val now = Date()
        return Jwts.builder()
            .setSubject(login)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + expiration))
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8)))
            .compact()
    }
    fun getUsernameFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }
}