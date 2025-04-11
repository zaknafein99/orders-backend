package com.kotlin.orders.controller.user

import com.kotlin.orders.entity.Role
import com.kotlin.orders.entity.User
import com.kotlin.orders.mapper.UserMapper
import com.kotlin.orders.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(private val userService: UserService, private val userMapper: UserMapper) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUser(@RequestBody @Valid userRequest: UserRequest): UserResponse {
        val user: User = User(null, userRequest.email, userRequest.password, userRequest.role)
        return userService.create(user)
    }

    @GetMapping
    //List all users
    fun listAll(): List<UserResponse> =
            userService.findAll()
                    .map { it.toResponse()!! }

    @GetMapping("/{uuid}")
    fun findByUuid(@PathVariable uuid: UUID): Any {

        val foundUser = userService.findByUUID(uuid)
        return foundUser?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

    }

    @DeleteMapping("/{uuid}")
    fun deleteByUuid(@PathVariable uuid: UUID): ResponseEntity<Void> {
        userService.deleteById(uuid)
        return ResponseEntity.ok().build()
    }

    private fun UserRequest.toModel() : User =
            User(
                    id = UUID.randomUUID(),
                    email = this.email,
                    password = this.password,
                    role = Role.USER
                    )

    private fun User.toResponse() : UserResponse? =
            this.id?.let {
                UserResponse(
                        id = it,
                        email = this.email,
                )
            }
}
