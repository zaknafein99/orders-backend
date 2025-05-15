package com.kotlin.orders.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class OrderItemDTO(
    val id: Int? = null,
    @field:NotNull
    val itemId: Int?,
    @field:NotNull
    val orderId: Int?,
    @field:NotNull
    @field:Min(1)
    val quantity: Int
) 