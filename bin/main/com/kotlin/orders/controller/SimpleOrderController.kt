package com.kotlin.orders.controller

import com.kotlin.orders.dto.OrderDTO
import com.kotlin.orders.entity.Customer
import com.kotlin.orders.entity.Item
import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.entity.Truck
import com.kotlin.orders.service.CustomerService
import com.kotlin.orders.service.ItemService
import com.kotlin.orders.service.OrderService
import com.kotlin.orders.service.TruckService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/simple-orders")
@CrossOrigin
class SimpleOrderController(
    private val customerService: CustomerService,
    private val itemService: ItemService,
    private val truckService: TruckService,
    private val orderService: OrderService
) {
    @PostMapping
    fun createSimpleOrder(@RequestBody orderRequest: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        try {
            // Extract data from the request
            val customerId = (orderRequest["customerId"] as? Number)?.toInt() ?: 1
            val truckId = (orderRequest["truckId"] as? Number)?.toInt() ?: 1
            
            // Get references to the customer and truck
            val customer = customerService.findById(customerId)
            val truck = truckService.findById(truckId)
            
            if (customer == null || truck == null) {
                val errorResponse: Map<String, Any> = mapOf(
                    "error" to "Customer or truck not found",
                    "customerId" to customerId,
                    "truckId" to truckId
                )
                return ResponseEntity.badRequest().body(errorResponse)
            }
            
            // Create a simple order with no items
            val order = Order(
                id = null,
                customer = customer,
                truck = truck,
                date = LocalDate.now(),
                totalPrice = 10.0, // Default price
                status = OrderStatus.PENDING,
                orderItems = mutableListOf()
            )
            
            // Save the order to the database
            val savedOrder = orderService.saveOrder(order)
            
            val customerMap: Map<String, Any> = mapOf(
                "id" to (savedOrder.customer.id ?: 0),
                "name" to (savedOrder.customer.name)
            )
            
            val truckMap: Map<String, Any> = mapOf(
                "id" to (savedOrder.truck.id ?: 0),
                "name" to (savedOrder.truck.name)
            )
            
            val responseMap: Map<String, Any> = mapOf(
                "id" to (savedOrder.id ?: 0),
                "customer" to customerMap,
                "truck" to truckMap,
                "date" to savedOrder.date.toString(),
                "totalPrice" to savedOrder.totalPrice,
                "status" to savedOrder.status.name
            )
            
            return ResponseEntity.ok(responseMap)
        } catch (e: Exception) {
            e.printStackTrace()
            val errorResponse: Map<String, Any> = mapOf(
                "error" to (e.message ?: "Unknown error")
            )
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
        }
    }
} 