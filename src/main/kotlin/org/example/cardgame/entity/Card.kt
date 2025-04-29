package org.example.cardgame.entity

import jakarta.persistence.*

@Entity
@Table(name = "card", schema = "public")
data class Card (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: CardType = CardType.POINTS,

    @Column(name = "value", nullable = false)
    val value: Int = 0
)

enum class CardType {
    POINTS, ACTION
}