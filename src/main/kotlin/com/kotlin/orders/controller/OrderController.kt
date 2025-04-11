package com.kotlin.orders.controller

import com.kotlin.orders.dto.OrderDTO
import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.service.OrderService

import jakarta.validation.Valid

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/orders")
@CrossOrigin
class OrderController(val orderService: OrderService) {

    private val logger: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody @Valid orderDTO: OrderDTO): ResponseEntity<OrderDTO> =
        ResponseEntity.ok(orderService.createOrder(orderDTO))

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getOrders(@PageableDefault(page = 0, size = 10) pageable: Pageable): Page<OrderDTO> =
        orderService.getOrders(pageable)

    @GetMapping("/pending")
    @ResponseStatus(HttpStatus.OK)
    fun getPendingOrders(): List<OrderDTO> =
        orderService.getPendingOrders()

    @GetMapping("/delivered")
    @ResponseStatus(HttpStatus.OK)
    fun getDeliveredOrders(): List<OrderDTO> =
        orderService.getDeliveredOrders()

    @PostMapping("/{orderId}/deliver")
    @ResponseStatus(HttpStatus.OK)
    fun markOrderAsDelivered(@PathVariable orderId: Int): OrderDTO =
        orderService.markAsDelivered(orderId)

    @PutMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.OK)
    fun updateOrderStatus(@PathVariable orderId: Int, @RequestBody statusRequest: Map<String, String>): OrderDTO {
        logger.info("Updating order status for order $orderId: ${statusRequest["status"]}")
        val status = statusRequest["status"] ?: throw IllegalArgumentException("Status is required")
        
        // If status is DELIVERED, mark the order as delivered
        if (status == "DELIVERED") {
            logger.info("Marking order $orderId as delivered")
            return orderService.markAsDelivered(orderId)
        }
        
        // For future: handle other status changes
        throw IllegalArgumentException("Unsupported status: $status")
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelOrder(@PathVariable orderId: Int) {
        logger.info("Received request to cancel order: $orderId")
        orderService.deleteOrder(orderId)
    }

    @GetMapping("/customer")
    @ResponseStatus(HttpStatus.OK)
    fun getOrdersByCustomer(@PageableDefault(page = 0, size = 10) pageable: Pageable,
                            @RequestParam phoneNumber: String): Page<OrderDTO> =
        orderService.getOrdersByCustomerPaged(pageable, phoneNumber)

    @GetMapping("/customer/orders")
    @ResponseStatus(HttpStatus.OK)
    fun getOrdersByCustomerPaged(@PageableDefault(page = 0, size = 10) pageable: Pageable, @RequestParam phoneNumber: String): Page<OrderDTO> =
        orderService.getOrdersByCustomerPaged(pageable, phoneNumber)

    @GetMapping("/truck/{truckId}")
    fun getOrdersByTruckIdAndDate(@PageableDefault(page = 0, size = 10) pageable: Pageable,
                                  @PathVariable truckId: Int,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) deliveryDate: LocalDate): ResponseEntity<Map<String, Any>> {
        val orders = orderService.getOrdersByTruckIdAndDate(truckId, deliveryDate, pageable)
        val dayTotalPrice = orderService.getTotalPriceByTruckIdAndDate(truckId, deliveryDate, pageable)
        val response = mapOf("orders" to orders, "dayTotalPrice" to dayTotalPrice)
        return ResponseEntity.ok().body(response)
    }
    @PostMapping("/{orderId}/truck")
    @ResponseStatus(HttpStatus.OK)
    fun assignTruckToOrder(@PathVariable orderId: Int, @RequestBody payload: Map<String, Int>): OrderDTO {
        val truckId = payload["truckId"] ?: throw IllegalArgumentException("truckId is required")
        return orderService.assignTruckToOrder(orderId, truckId)
    }
}
