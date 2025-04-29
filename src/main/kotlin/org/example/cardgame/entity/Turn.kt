package org.example.cardgame.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "turn", schema = "public")
data class Turn(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "game_session_id", nullable = false)
    val gameSession: GameSession = GameSession(),

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    val player: GamePlayer = GamePlayer(),

    @ManyToOne
    @JoinColumn(name = "card_id")
    val playedCard: Card = Card(),

    @Column(name = "points_change", nullable = false)
    var pointsChange: Int = 0,

    @ManyToOne
    @JoinColumn(name = "target_player_id")
    var targetPlayer: GamePlayer? = null,

    @Column(name = "turn_number", nullable = false)
    var turnNumber: Int = 0
)