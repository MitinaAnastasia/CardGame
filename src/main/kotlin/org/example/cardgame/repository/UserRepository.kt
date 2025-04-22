package org.example.cardgame.repository

import org.example.cardgame.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByLogin(login: String): User?
    fun existsByLogin(login: String): Boolean
    fun existsByUsername(username: String): Boolean
}