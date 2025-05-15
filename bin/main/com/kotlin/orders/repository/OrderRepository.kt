package com.kotlin.orders.repository

import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : CrudRepository<Order, Int> {

    fun findByCustomerPhoneNumber(phoneNumber: String): List<Order>

    fun findByCustomerPhoneNumber(phoneNumber: String, pageable: Pageable): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.truck.id = :truckId AND o.date >= :startOfDay AND o.date < :endOfDay")
    fun findByTruckIdAndDate(@Param("truckId") truckId: Int,
                             @Param("startOfDay") startOfDay: LocalDate,
                             @Param("endOfDay") endOfDay: LocalDate, pageable: Pageable): Page<Order>

    fun findAll(pageable: Pageable): Page<Order>

    fun findByStatus(status: OrderStatus): List<Order>

    fun findByDate(date: LocalDate): List<Order>

    fun findByDateBetween(startDate: LocalDate, endDate: LocalDate): List<Order>
}