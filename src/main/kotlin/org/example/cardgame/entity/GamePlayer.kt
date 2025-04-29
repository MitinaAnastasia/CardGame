package org.example.cardgame.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "game_player", schema = "public")
data class GamePlayer (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false)
    val gameSession: GameSession = GameSession(),

    @Column(name = "points", nullable = false)
    var points: Int = 0,

    @Column(name = "is_blocked", nullable = false)
    var isBlocked: Boolean = false,

    @Column(name = "join_order", nullable = false)
    val joinOrder: Int = 0,
)
