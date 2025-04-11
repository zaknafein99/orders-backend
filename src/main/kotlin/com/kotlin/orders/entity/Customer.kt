package com.kotlin.orders.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@Table(name = "customers")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Customer (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var name: String,
    var address: String,
    var phoneNumber: String,
    var type: String,
    var state: String,
    @OneToMany(mappedBy = "customer", cascade = [CascadeType.REMOVE])
    var orders: List<Order>

)
