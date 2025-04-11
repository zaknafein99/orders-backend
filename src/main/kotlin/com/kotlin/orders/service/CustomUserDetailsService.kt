package com.kotlin.orders.service

import com.kotlin.orders.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

typealias ApplicationUser = com.kotlin.orders.entity.User

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val foundUser: com.kotlin.orders.entity.User = userRepository.findByEmail(username) as com.kotlin.orders.entity.User
        val userDetails = foundUser.mapToUserDetails()
        return userDetails

    }



    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User
            .builder()
            .username(email)
            .password(password)
            .roles(role.toString())
            .build()
}