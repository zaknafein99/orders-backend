package com.kotlin.orders.service

import com.kotlin.orders.dto.OrderDTO
import com.kotlin.orders.entity.Order
import com.kotlin.orders.entity.OrderItem
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.mapper.OrderMapper
import com.kotlin.orders.repository.CustomerRepository
import com.kotlin.orders.repository.ItemRepository
import com.kotlin.orders.repository.OrderRepository
import com.kotlin.orders.repository.TruckRepository
import jakarta.persistence.EntityNotFoundException
import java.time.LocalDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
        private val customerRepository: CustomerRepository,
        private val truckRepository: TruckRepository,
        private val itemRepository: ItemRepository,
        private val orderRepository: OrderRepository,
        private val orderMapper: OrderMapper
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun createOrder(orderDTO: OrderDTO): OrderDTO {
        val customer =
                customerRepository.findById(orderDTO.customer.id!!).orElseThrow {
                    EntityNotFoundException("Customer not found with ID ${orderDTO.customer.id}")
                }

        val truck =
                truckRepository.findById(orderDTO.truck.id!!).orElseThrow {
                    EntityNotFoundException("Truck not found with ID ${orderDTO.truck.id}")
                }
                
        // Create the order first
        val order = Order(
            id = null,
            customer = customer,
            truck = truck,
            date = LocalDate.now(),
            totalPrice = orderDTO.items.sumOf { it.price * it.quantity },
            status = OrderStatus.PENDING,
            orderItems = mutableListOf() // Start with empty mutable list
        )
        
        // Save the order to get an ID
        val savedOrder = orderRepository.save(order)
        
        // Now create the order items
        orderDTO.items.forEach { itemDTO ->
            val item = itemRepository.findById(itemDTO.id!!).orElseThrow {
                EntityNotFoundException("Item not found with ID ${itemDTO.id}")
            }
            
            val orderItem = OrderItem(
                id = null,
                order = savedOrder,
                item = item,
                quantity = itemDTO.quantity
            )
            
            // Add to the order's item collection
            savedOrder.orderItems.add(orderItem)
        }
        
        // Save the order again with all its items
        val finalOrder = orderRepository.save(savedOrder)
        
        return orderMapper.toDto(finalOrder)
    }

    fun getOrders(pageable: Pageable): Page<OrderDTO> {
        return orderRepository.findAll(pageable).map { orderMapper.toDto(it) }
    }

    fun getPendingOrders(): List<OrderDTO> {
        println("Fetching all pending orders from repository")
        val pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING)
        println("Found ${pendingOrders.size} pending orders: ${pendingOrders.map { it.id }}")
        return pendingOrders.map { orderMapper.toDto(it) }
    }

    fun getDeliveredOrders(): List<OrderDTO> {
        return orderRepository.findByStatus(OrderStatus.DELIVERED).map { orderMapper.toDto(it) }
    }

    @Transactional
    fun markOrderAsDelivered(orderId: Int): OrderDTO {
        logger.info("Marking order as delivered: $orderId")
        val order =
                orderRepository.findById(orderId).orElseThrow {
                    EntityNotFoundException("Order not found with ID $orderId")
                }

        // Update inventory quantities for all items in the order
        order.orderItems.forEach { orderItem ->
            val item = orderItem.item
            val orderQuantity = orderItem.quantity
            
            logger.info("Updating inventory for item ${item.id} (${item.name}): current quantity ${item.quantity}")
            
            // Calculate new quantity
            val currentQuantity = item.quantity
            val newQuantity = Math.max(0, currentQuantity - orderQuantity)
            
            logger.info("Item ${item.id} (${item.name}): ${currentQuantity} - ${orderQuantity} = ${newQuantity}")
            
            // Update the item with new quantity
            item.quantity = newQuantity
            itemRepository.save(item)
            logger.info("Item ${item.id} quantity updated to ${newQuantity}")
        }

        // Update order status to DELIVERED
        order.status = OrderStatus.DELIVERED
        return orderMapper.toDto(orderRepository.save(order))
    }

    fun getOrdersByCustomer(phoneNumber: String): List<Order> {
        return orderRepository.findByCustomerPhoneNumber(phoneNumber)
    }

    fun getOrdersByCustomerPaged(pageable: Pageable, phoneNumber: String): Page<OrderDTO> {
        return orderRepository.findByCustomerPhoneNumber(phoneNumber, pageable).map {
            orderMapper.toDto(it)
        }
    }

    fun getOrdersByTruckIdAndDate(
            truckId: Int,
            deliveryDate: LocalDate,
            pageable: Pageable
    ): Page<Order> {
        val endOfDay = deliveryDate.plusDays(1)
        return orderRepository.findByTruckIdAndDate(truckId, deliveryDate, endOfDay, pageable)
    }

    fun getTotalPriceByTruckIdAndDate(
            truckId: Int,
            deliveryDate: LocalDate,
            pageable: Pageable
    ): Double {
        return getOrdersByTruckIdAndDate(truckId, deliveryDate, pageable).content.sumOf {
            it.totalPrice
        }
    }

    fun assignTruckToOrder(orderId: Int, truckId: Int): OrderDTO {
        val order =
                orderRepository.findById(orderId).orElseThrow {
                    EntityNotFoundException("Order not found with ID $orderId")
                }

        val truck =
                truckRepository.findById(truckId).orElseThrow {
                    EntityNotFoundException("Truck not found with ID $truckId")
                }

        order.truck = truck
        return orderMapper.toDto(orderRepository.save(order))
    }

    fun markAsDelivered(orderId: Int): OrderDTO {
        return markOrderAsDelivered(orderId)
    }

    fun deleteOrder(orderId: Int) {
        println("Attempting to delete order with ID: $orderId")
        val order = orderRepository.findById(orderId).orElseThrow {
            val errorMessage = "Order not found with ID $orderId"
            println(errorMessage)
            EntityNotFoundException(errorMessage)
        }
        println("Found order to delete: ${order.id}, status: ${order.status}")
        try {
            orderRepository.delete(order)
            println("Order $orderId successfully deleted from repository")
        } catch (e: Exception) {
            println("Error deleting order $orderId: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    @Transactional
    fun saveOrder(order: Order): Order {
        return orderRepository.save(order)
    }

    fun findById(id: Int): Order? {
        return orderRepository.findById(id).orElse(null)
    }
}
