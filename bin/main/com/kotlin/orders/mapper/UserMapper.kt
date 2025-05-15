package com.kotlin.orders.mapper

import com.kotlin.orders.controller.user.UserRequest
import com.kotlin.orders.controller.user.UserResponse
import com.kotlin.orders.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {

    fun userToUserResponse(user: User): UserResponse

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", constant = "")
    fun userResponseToUser(userResponse: UserResponse): User

    fun userToUserRequest(user: User): UserRequest

    @Mapping(target = "id", ignore = true)
    fun userRequestToUser(userRequest: UserRequest): User

    fun userRequestToUserResponse(userRequest: UserRequest): UserResponse

    fun userResponseToUserRequest(userResponse: UserResponse): UserRequest
}