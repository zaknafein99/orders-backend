package com.kotlin.orders.dto

import com.kotlin.orders.entity.Item
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.entity.Truck
import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class OrderDTO(
    val id: Int?,
    @get:NotNull(message = "El cliente no puede estar vacío")
    val customer: CustomerDTO,
    @get:NotNull(message = "Debe haber al menos un producto")
    val items: List<ItemDTO>,
    val truck: TruckDTO,
    val date: LocalDate = LocalDate.now(),
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING
)