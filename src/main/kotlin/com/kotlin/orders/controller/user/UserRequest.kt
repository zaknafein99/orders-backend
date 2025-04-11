package com.kotlin.orders.controller.user

import com.kotlin.orders.entity.Role

data class UserRequest(
        val email: String,
        val password: String,
        val role: Role
)
