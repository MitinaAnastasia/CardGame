package org.example.cardgame.repository

import org.example.cardgame.entity.GameSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameSessionRepository : JpaRepository<GameSession, Long>
