package com.kotlin.orders.service

import com.kotlin.orders.config.JwtProperties
import com.kotlin.orders.controller.auth.AuthenticationRequest
import com.kotlin.orders.controller.auth.AuthenticationResponse
import com.kotlin.orders.repository.RefreshTokenRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
        private val authManager: AuthenticationManager,
        private val userDetailsService: CustomUserDetailsService,
        private val tokenService: TokenService,
        private val jwtProperties: JwtProperties,
        private val refreshTokenRepository: RefreshTokenRepository
) {
    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        authRequest.email,
                        authRequest.password
                )
        )

        val foundUser = userDetailsService.loadUserByUsername(authRequest.email)
        val accessToken = generateAccessToken(foundUser)

        val refreshToken = generateRefreshToken(foundUser)

        refreshTokenRepository.save(refreshToken, foundUser)

        return AuthenticationResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
        )

    }

    private fun generateRefreshToken(foundUser: UserDetails) = tokenService.generate(
        userDetails = foundUser,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    )

    private fun generateAccessToken(foundUser: UserDetails) = tokenService.generate(
        userDetails = foundUser,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    )

    fun refreshAccessToken(token: String): String? {
        val extractedEmail = tokenService.extractEmail(token)

        return extractedEmail.let { email ->
            val foundUser = userDetailsService.loadUserByUsername(email)
            val foundRefreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

            if (!tokenService.isExpired(token) && foundUser.username == foundRefreshTokenUserDetails?.username)
                generateAccessToken(foundUser)
            else
                null

        }
    }
}
