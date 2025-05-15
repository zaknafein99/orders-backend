package com.kotlin.orders.controller

import com.kotlin.orders.dto.CustomerDTO
import com.kotlin.orders.dto.ItemDTO
import com.kotlin.orders.dto.OrderDTO
import com.kotlin.orders.dto.TruckDTO
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.service.ItemService
import com.kotlin.orders.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/test")
@CrossOrigin
class TestInventoryController(
    private val orderService: OrderService,
    private val itemService: ItemService
) {
    private val logger = LoggerFactory.getLogger(TestInventoryController::class.java)

    @PostMapping("/inventory-update-test")
    fun testInventoryUpdate(): ResponseEntity<Map<String, Any>> {
        logger.info("Starting inventory update test")
        
        // 1. Get an item to use in our test
        val items = itemService.getItems()
        if (items.isEmpty()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "No items available for testing"))
        }
        
        val testItem = items.first()
        val initialQuantity = testItem.quantity
        
        logger.info("Using test item: $testItem with initial quantity: $initialQuantity")
        
        // 2. Create a test order with this item
        val orderDTO = OrderDTO(
            id = null,
            customer = CustomerDTO(
                id = 1, // Assuming there's at least one customer with ID 1
                name = "Test Customer",
                address = "Test Address",
                phoneNumber = "1234567890",
                type = "E",
                state = "A"
            ),
            items = listOf(
                testItem.copy(quantity = 1) // Order 1 of this item
            ),
            truck = TruckDTO(
                id = 1, // Assuming there's at least one truck with ID 1
                name = "Test Truck"
            ),
            date = LocalDate.now(),
            totalPrice = testItem.price,
            status = OrderStatus.PENDING
        )
        
        logger.info("Creating test order with item: ${testItem.id}")
        val createdOrder = orderService.createOrder(orderDTO)
        logger.info("Created order with ID: ${createdOrder.id}")
        
        // 3. Mark the order as delivered, which should update inventory
        logger.info("Marking order as delivered")
        val deliveredOrder = orderService.markAsDelivered(createdOrder.id!!)
        logger.info("Order marked as delivered: ${deliveredOrder.status}")
        
        // 4. Get the updated item to check its quantity
        val updatedItem = itemService.getItems().first { it.id == testItem.id }
        val newQuantity = updatedItem.quantity
        
        logger.info("Item quantity before: $initialQuantity, after: $newQuantity")
        
        return ResponseEntity.ok(mapOf(
            "success" to true,
            "orderId" to (deliveredOrder.id ?: 0),
            "itemId" to (testItem.id ?: 0),
            "initialQuantity" to initialQuantity,
            "newQuantity" to newQuantity,
            "quantityReduced" to (initialQuantity > newQuantity)
        ))
    }
} 