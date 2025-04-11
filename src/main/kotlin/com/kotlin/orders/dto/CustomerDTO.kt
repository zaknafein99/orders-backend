package com.kotlin.orders.dto

import jakarta.validation.constraints.NotBlank

data class CustomerDTO(
    val id: Int?,
    @get:NotBlank(message = "El nombre no puede estar vacío")
    val name: String,
    @get:NotBlank(message = "La dirección no puede estar vacía")
    val address: String,
    @get:NotBlank(message = "El número de teléfono no puede estar vacío")
    val phoneNumber: String,
    val type: String,
    val state: String
)