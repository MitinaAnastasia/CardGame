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
@Table(name = "game_card")
data class GameCard(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "card_id")
    val card: Card = Card(),

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    val gameSession: GameSession = GameSession(),

    @Column(name = "position_in_deck", nullable = false)
    var positionInDeck: Int = 0,

    @Column(name = "is_used", nullable = false)
    var isUsed: Boolean = false
)