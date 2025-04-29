package org.example.cardgame.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

@Entity
@Table(name = "game_session", schema = "public")
data class GameSession (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: GameStatus = GameStatus.WAITING_FOR_PLAYERS,

    @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL])
    val players: MutableSet<GamePlayer> = mutableSetOf(),

    @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL])
    val deck: MutableList<GameCard> = mutableListOf(),

    @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL])
    val turns: MutableList<Turn> = mutableListOf(),

    @Column(name = "current_player_index", nullable = false)
    var currentPlayerIndex: Int = 0,
    @Column(name = "max_points", nullable = false)
    var maxPoints: Int = 30
)
enum class GameStatus {
    WAITING_FOR_PLAYERS, IN_PROGRESS, FINISHED
}