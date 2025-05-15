package com.kotlin.orders.entity

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "items")
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    var name: String,
    var description: String,
    var price: Double,
    var quantity: Int,
    var category: String,
    
    @OneToMany(mappedBy = "item", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    val orderItems: MutableList<OrderItem> = mutableListOf()
)