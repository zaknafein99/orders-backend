package com.kotlin.orders.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    val customer: Customer,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    val orderItems: MutableList<OrderItem> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "truck_id")
    var truck: Truck,

    val date: LocalDate,

    val totalPrice: Double,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING
) {
    // Helper property to get all items (for backward compatibility)
    @get:Transient
    val items: List<Item>
        get() = orderItems.map { it.item }
}

enum class OrderStatus {
    PENDING,
    DELIVERED
}