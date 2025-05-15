package com.kotlin.orders.entity

import jakarta.persistence.*

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order,
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    val item: Item,
    
    val quantity: Int
) 