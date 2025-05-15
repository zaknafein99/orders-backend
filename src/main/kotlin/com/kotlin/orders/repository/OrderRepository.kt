package com.kotlin.orders.repository

import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface OrderRepository : JpaRepository<Order, Int> {

    fun findByCustomerPhoneNumber(phoneNumber: String): List<Order>

    fun findByCustomerPhoneNumber(phoneNumber: String, pageable: Pageable): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.truck.id = :truckId AND o.date >= :startOfDay AND o.date < :endOfDay")
    fun findByTruckIdAndDate(
        @Param("truckId") truckId: Int,
        @Param("startOfDay") startOfDay: LocalDate,
        @Param("endOfDay") endOfDay: LocalDate,
        pageable: Pageable
    ): Page<Order>

    @Query("SELECT o FROM Order o WHERE o.truck.id = :truckId AND o.date >= :startOfDay AND o.date < :endOfDay")
    fun findByTruckIdAndDate(
        @Param("truckId") truckId: Int,
        @Param("startOfDay") startOfDay: LocalDate,
        @Param("endOfDay") endOfDay: LocalDate
    ): List<Order>

    @Query("SELECT o FROM Order o WHERE o.truck.id = :truckId AND o.date >= :startDate AND o.date <= :endDate")
    fun findByTruckIdAndDateBetween(
        @Param("truckId") truckId: Int,
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Order>

    @Query("SELECT o FROM Order o WHERE o.date = :date")
    fun findByDate(@Param("date") date: LocalDate): List<Order>

    @Query("SELECT o FROM Order o WHERE o.date BETWEEN :startDate AND :endDate")
    fun findByDateBetween(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate
    ): List<Order>

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    fun findByStatus(@Param("status") status: OrderStatus): List<Order>

    override fun findAll(pageable: Pageable): Page<Order>

    override fun findAll(): List<Order>
}