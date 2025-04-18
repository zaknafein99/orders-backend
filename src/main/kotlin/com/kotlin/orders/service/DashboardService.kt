package com.kotlin.orders.service

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.kotlin.orders.repository.OrderRepository
import com.kotlin.orders.repository.TruckRepository
import com.kotlin.orders.entity.OrderStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Service
class DashboardService(
    private val orderRepository: OrderRepository,
    private val truckRepository: TruckRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(DashboardService::class.java)

    fun getDailyTruckSales(date: LocalDate): Map<String, Any> {
        val orders = orderRepository.findByDate(date)
        val truckSales = orders.groupBy { it.truck?.id }
            .mapValues { (_, orders) -> 
                mapOf(
                    "totalSales" to orders.sumOf { it.totalPrice },
                    "orderCount" to orders.size
                )
            }
        return mapOf(
            "date" to date,
            "truckSales" to truckSales,
            "totalSales" to orders.sumOf { it.totalPrice },
            "totalOrders" to orders.size
        )
    }

    fun getWeeklyTruckSales(startDate: LocalDate): Map<String, Any> {
        val endDate = startDate.plusDays(6)
        val orders = orderRepository.findByDateBetween(startDate, endDate)
        val dailySales = (0..6).map { dayOffset ->
            val date = startDate.plusDays(dayOffset.toLong())
            val dayOrders = orders.filter { it.date == date }
            mapOf(
                "date" to date,
                "totalSales" to dayOrders.sumOf { it.totalPrice },
                "orderCount" to dayOrders.size
            )
        }
        return mapOf(
            "startDate" to startDate,
            "endDate" to endDate,
            "dailySales" to dailySales,
            "totalSales" to orders.sumOf { it.totalPrice },
            "totalOrders" to orders.size
        )
    }

    fun getMonthlyTruckSales(date: LocalDate): Map<String, Any> {
        val startOfMonth = date.withDayOfMonth(1)
        val endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth())
        val orders = orderRepository.findByDateBetween(startOfMonth, endOfMonth)
        val dailySales = (0 until date.lengthOfMonth()).map { dayOffset ->
            val currentDate = startOfMonth.plusDays(dayOffset.toLong())
            val dayOrders = orders.filter { it.date == currentDate }
            mapOf(
                "date" to currentDate,
                "totalSales" to dayOrders.sumOf { it.totalPrice },
                "orderCount" to dayOrders.size
            )
        }
        return mapOf(
            "month" to date.monthValue,
            "year" to date.year,
            "dailySales" to dailySales,
            "totalSales" to orders.sumOf { it.totalPrice },
            "totalOrders" to orders.size
        )
    }

    fun getMostUsedControls(): Map<String, Int> {
        // This would typically be implemented with a usage tracking system
        // For now, returning mock data
        return mapOf(
            "createOrder" to 150,
            "searchCustomer" to 300,
            "viewOrders" to 200,
            "manageInventory" to 100,
            "assignTruck" to 80
        )
    }

    fun getDashboardStatistics(): Map<String, Any> {
        try {
            val today = LocalDate.now()
            logger.info("Fetching dashboard statistics for today: $today")
            
            val startOfMonth = today.withDayOfMonth(1)
            val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())
            
            logger.info("Fetching orders for date range: $startOfMonth to $endOfMonth")
            
            try {
                val todayOrders = orderRepository.findByDate(today)
                logger.info("Found ${todayOrders.size} orders for today")
                
                val monthOrders = orderRepository.findByDateBetween(startOfMonth, endOfMonth)
                logger.info("Found ${monthOrders.size} orders for the month")
                
                logger.info("Fetching pending orders with status: ${OrderStatus.PENDING}")
                val pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING)
                logger.info("Found ${pendingOrders.size} pending orders")
                
                val totalTrucks = truckRepository.count()
                logger.info("Total trucks: $totalTrucks")
                
                // If no orders for the month, provide empty dailySales array instead of null
                val dailySales = if (monthOrders.isNotEmpty()) {
                    monthOrders.groupBy { it.date }
                        .map { (date, orders) ->
                            mapOf(
                                "date" to date.toString(), // Convert to string to ensure JSON serialization works
                                "totalSales" to orders.sumOf { it.totalPrice },
                                "orderCount" to orders.size
                            )
                        }.sortedBy { it["date"] as String }
                } else {
                    // Return empty list if no orders
                    logger.info("No orders found for the month, returning empty dailySales array")
                    emptyList()
                }
                
                logger.info("Prepared ${dailySales.size} daily sales records")

                return mapOf(
                    "today" to mapOf(
                        "totalSales" to (todayOrders.sumOf { it.totalPrice } ?: 0.0),
                        "orderCount" to todayOrders.size
                    ),
                    "month" to mapOf(
                        "totalSales" to (monthOrders.sumOf { it.totalPrice } ?: 0.0),
                        "orderCount" to monthOrders.size,
                        "dailySales" to dailySales
                    ),
                    "pendingOrders" to pendingOrders.size,
                    "totalTrucks" to totalTrucks,
                    "averageOrderValue" to if (monthOrders.isNotEmpty()) 
                        monthOrders.sumOf { it.totalPrice } / monthOrders.size 
                    else 0.0
                )
            } catch (e: Exception) {
                logger.error("Error processing orders data", e)
                throw e
            }
        } catch (e: Exception) {
            logger.error("Error fetching dashboard statistics", e)
            // Return default empty values instead of throwing exception
            return mapOf(
                "today" to mapOf(
                    "totalSales" to 0.0,
                    "orderCount" to 0
                ),
                "month" to mapOf(
                    "totalSales" to 0.0,
                    "orderCount" to 0,
                    "dailySales" to emptyList<Map<String, Any>>()
                ),
                "pendingOrders" to 0,
                "totalTrucks" to 0,
                "averageOrderValue" to 0.0
            )
        }
    }

    fun generateDailyTruckSalesPDF(date: LocalDate): ByteArray {
        val salesData = getDailyTruckSales(date)
        val outputStream = ByteArrayOutputStream()
        val writer = PdfWriter(outputStream)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        // Add title
        val title = Paragraph("Daily Truck Sales Report - ${date.format(DateTimeFormatter.ISO_DATE)}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16f)
        document.add(title)
        document.add(Paragraph("\n"))

        // Create table
        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 30f, 30f)))
            .setWidth(UnitValue.createPercentValue(100f))

        // Add headers
        table.addHeaderCell("Truck")
        table.addHeaderCell("Orders")
        table.addHeaderCell("Total Sales")

        // Add data
        salesData["truckSales"]?.let { truckSales ->
            (truckSales as? Map<*, *>)?.forEach { (truckId, sales) ->
                val salesMap = sales as? Map<*, *>
                table.addCell("Truck $truckId")
                table.addCell(salesMap?.get("orderCount")?.toString() ?: "0")
                table.addCell("$${salesMap?.get("totalSales")?.toString() ?: "0.00"}")
            }
        }

        document.add(table)

        // Add summary
        document.add(Paragraph("\n"))
        val totalOrders = (salesData["truckSales"] as? Map<*, *>)?.values?.sumOf { 
            ((it as? Map<*, *>)?.get("orderCount") as? Number)?.toInt() ?: 0 
        } ?: 0
        val totalSales = (salesData["truckSales"] as? Map<*, *>)?.values?.sumOf { 
            ((it as? Map<*, *>)?.get("totalSales") as? Number)?.toDouble() ?: 0.0 
        } ?: 0.0

        document.add(Paragraph("Summary:"))
        document.add(Paragraph("Total Orders: $totalOrders"))
        document.add(Paragraph("Total Sales: $${String.format("%.2f", totalSales)}"))

        document.close()
        return outputStream.toByteArray()
    }
} 