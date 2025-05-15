package com.kotlin.orders.controller.auth


data class AuthenticationRequest(
        val email: String,
        val password: String
) {

}
