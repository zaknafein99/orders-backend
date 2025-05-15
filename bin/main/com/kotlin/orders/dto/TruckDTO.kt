package com.kotlin.orders.dto

data class TruckDTO(
    val id: Int?,
    val name: String = "Default Truck"
//    val orders: List<OrderDTO> = mutableListOf()
)