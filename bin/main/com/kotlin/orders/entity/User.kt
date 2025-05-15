package com.kotlin.orders.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue
    val id: UUID?,
    var email: String,
    var password: String,
    @Enumerated(EnumType.STRING)
    val role: Role
)

enum class Role {
    USER,
    ADMIN
}
