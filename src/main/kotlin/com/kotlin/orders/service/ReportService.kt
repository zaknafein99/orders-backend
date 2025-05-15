package com.kotlin.orders.service

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.kotlin.orders.entity.OrderStatus
import com.kotlin.orders.repository.OrderRepository
import com.kotlin.orders.repository.TruckRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val orderRepository: OrderRepository,
    private val truckRepository: TruckRepository
) {
    private val logger = LoggerFactory.getLogger(ReportService::class.java)

    /**
     * Generate a PDF report for truck deliveries
     */
    fun generateTruckDeliveryReport(truckId: Int, startDate: LocalDate, endDate: LocalDate): ByteArray {
        logger.info("Generating truck delivery report for truck: $truckId, period: $startDate to $endDate")
        
        // Fetch the truck
        val truck = truckRepository.findById(truckId)
            .orElseThrow { EntityNotFoundException("Truck not found with ID: $truckId") }
        
        // Fetch orders for this truck in the date range
        val orders = orderRepository.findByTruckIdAndDateBetween(
            truckId = truckId,
            startDate = startDate,
            endDate = endDate
        )
        
        logger.info("Found ${orders.size} orders for truck ${truck.name} in date range")
        
        // Create PDF
        val baos = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(baos)
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)
        
        // Add title
        document.add(
            Paragraph("Truck Delivery Report: ${truck.name}")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18f)
        )
        
        // Add date range
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        document.add(
            Paragraph("Period: ${startDate.format(dateFormatter)} to ${endDate.format(dateFormatter)}")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12f)
        )
        
        // Create table
        // New table structure for order-level summary
        val table = Table(UnitValue.createPercentArray(floatArrayOf(12f, 20f, 20f, 10f, 13f, 12f, 13f)))
            .setWidth(UnitValue.createPercentValue(100f))
        
        // Add header row
        table.addHeaderCell(Cell().add(Paragraph("Date")))
        table.addHeaderCell(Cell().add(Paragraph("Customer")))
        table.addHeaderCell(Cell().add(Paragraph("Address")))
        table.addHeaderCell(Cell().add(Paragraph("Items (Qty)")))
        table.addHeaderCell(Cell().add(Paragraph("Items Subtotal")))
        table.addHeaderCell(Cell().add(Paragraph("Delivery Fee")))
        table.addHeaderCell(Cell().add(Paragraph("Order Total")))
        
        // Add data rows
        var grandTotalAmount = 0.0
        var totalOrdersCount = orders.size // Corrected variable name for clarity
        var grandTotalItemsQuantity = 0
        
        orders.forEach { order ->
            val orderItemsQuantity = order.orderItems.sumOf { it.quantity }
            val itemsSubtotal = order.totalPrice // This is already the sum of item prices * quantities
            val deliveryFee = order.flete ?: 0.0
            val orderTotal = itemsSubtotal + deliveryFee

            table.addCell(Cell().add(Paragraph(order.date.format(dateFormatter))))
            table.addCell(Cell().add(Paragraph(order.customer.name)))
            table.addCell(Cell().add(Paragraph(order.customer.address)))
            table.addCell(Cell().add(Paragraph(orderItemsQuantity.toString())))
            table.addCell(Cell().add(Paragraph(String.format("%.2f", itemsSubtotal))))
            table.addCell(Cell().add(Paragraph(String.format("%.2f", deliveryFee))))
            table.addCell(Cell().add(Paragraph(String.format("%.2f", orderTotal))))
            
            grandTotalItemsQuantity += orderItemsQuantity
            grandTotalAmount += orderTotal
        }
        
        // Add summary
        document.add(table)
        
        document.add(
            Paragraph("\nSUMMARY")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(14f)
                .setBold()
        )
        
        document.add(Paragraph("Total Amount (incl. Delivery Fees): $${String.format("%.2f", grandTotalAmount)}"))
        document.add(Paragraph("Total Orders: $totalOrdersCount"))
        document.add(Paragraph("Total Items (Overall Quantity): $grandTotalItemsQuantity"))
        
        if (totalOrdersCount > 0) {
            document.add(Paragraph("Average Order Value (incl. Delivery Fees): $${String.format("%.2f", grandTotalAmount / totalOrdersCount)}"))
        }
        
        // Close the document
        document.close()
        
        return baos.toByteArray()
    }
    
    /**
     * Generate a truck delivery report as a map
     */
    fun getTruckDeliveryReport(truckId: Int, startDate: LocalDate, endDate: LocalDate): Map<String, Any> {
        logger.info("Getting truck delivery report data for truck: $truckId, period: $startDate to $endDate")
        
        // Fetch the truck
        val truck = truckRepository.findById(truckId)
            .orElseThrow { EntityNotFoundException("Truck not found with ID: $truckId") }
        
        // Fetch orders for this truck in the date range
        val orders = orderRepository.findByTruckIdAndDateBetween(
            truckId = truckId,
            startDate = startDate,
            endDate = endDate
        )
        
        logger.info("Found ${orders.size} orders for truck ${truck.name} in date range")
        
        // Map orders to their details
        val orderDetails = orders.map { order ->
            mapOf(
                "id" to order.id,
                "date" to order.date,
                "customer" to mapOf(
                    "id" to order.customer.id,
                    "name" to order.customer.name,
                    "address" to order.customer.address,
                    "phoneNumber" to order.customer.phoneNumber
                ),
                "items" to order.orderItems.map { item ->
                    mapOf(
                        "id" to item.item.id,
                        "name" to item.item.name,
                        "quantity" to item.quantity,
                        "price" to item.item.price
                    )
                },
                "flete" to (order.flete ?: 0.0),
                "total" to order.totalPrice
            )
        }
        
        // Calculate totals
        val totalAmount = orders.sumOf { it.totalPrice }
        val totalOrders = orders.size
        val averageOrderValue = if (totalOrders > 0) totalAmount / totalOrders else 0.0
        
        return mapOf(
            "truck" to mapOf(
                "id" to truck.id,
                "name" to truck.name
            ),
            "period" to mapOf(
                "startDate" to startDate,
                "endDate" to endDate
            ),
            "orders" to orderDetails,
            "summary" to mapOf(
                "totalAmount" to totalAmount,
                "totalOrders" to totalOrders,
                "averageOrderValue" to averageOrderValue
            )
        )
    }
} 