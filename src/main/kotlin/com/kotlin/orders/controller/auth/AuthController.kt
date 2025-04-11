package com.kotlin.orders.controller.auth

import com.kotlin.orders.service.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
        private val authenticationService: AuthenticationService
) {

    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
            authenticationService.authentication(authRequest)

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshTokenRequest): TokenResponse =
            authenticationService.refreshAccessToken(request.token)
                ?.mapToTokenResponse()
                ?: throw RuntimeException("Refresh token is not valid")

    private fun String.mapToTokenResponse() = TokenResponse(this)

}