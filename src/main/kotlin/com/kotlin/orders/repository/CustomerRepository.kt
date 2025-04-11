package com.kotlin.orders.repository

import com.kotlin.orders.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Int> {

    fun findByPhoneNumber(phoneNumber: String): List<Customer>

}