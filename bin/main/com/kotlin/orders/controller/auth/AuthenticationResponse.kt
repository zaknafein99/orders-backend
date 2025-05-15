package com.kotlin.orders.controller.auth

data class AuthenticationResponse(
        val accessToken: String,
        val refreshToken: String
) {

}
