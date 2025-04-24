package org.example.cardgame.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType

@Entity
@Table(name = "user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id : Long = 0,
    @Column(name = "username", nullable = false, unique = true)
    val username: String = "",

    @Column(name = "login", nullable = false, unique = true)
    val login: String = "",

    @Column(name = "password", nullable = false)
    var password: String = "",

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val gamePlayers: MutableSet<GamePlayer> = mutableSetOf()
)