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
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReportService(
    private val orderRepository: OrderRepository,
    private val truckRepository: TruckRepository
) {
    fun generateTruckDeliveryReport(truckId: Int, startDate: LocalDate, endDate: LocalDate): ByteArray {
        val truck = truckRepository.findById(truckId).orElseThrow {
            EntityNotFoundException("Truck not found with ID $truckId")
        }

        val orders = orderRepository.findByTruckIdAndDate(
            truckId,
            startDate,
            endDate
        ).filter { order -> order.status == OrderStatus.DELIVERED }

        val outputStream = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(outputStream)
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)

        // Add title
        val title = Paragraph("Liquidacion Movil ${truck.id}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20f)
            .setBold()
        document.add(title)

        // Add date range
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateRange = Paragraph("Período: ${startDate.format(dateFormatter)} - ${endDate.format(dateFormatter)}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
        document.add(dateRange)

        // Create table
        val table = Table(UnitValue.createPercentArray(8)).useAllAvailableWidth()
        
        // Add headers
        val headers = listOf("Fecha", "Cliente", "Dirección", "Artículo", "Cantidad", "Móvil", "Flete", "Total")
        headers.forEach { header ->
            table.addHeaderCell(Cell().add(Paragraph(header)).setBold())
        }

        // Add data rows
        orders.forEach { order ->
            order.orderItems.forEach { item ->
                table.addCell(Cell().add(Paragraph(order.date.format(dateFormatter))))
                table.addCell(Cell().add(Paragraph(order.customer.name)))
                table.addCell(Cell().add(Paragraph(order.customer.address)))
                table.addCell(Cell().add(Paragraph(item.item.name)))
                table.addCell(Cell().add(Paragraph(item.quantity.toString())))
                table.addCell(Cell().add(Paragraph(truck.id.toString())))
                table.addCell(Cell().add(Paragraph(order.flete.toString())))
                table.addCell(Cell().add(Paragraph(order.totalPrice.toString())))
            }
        }

        document.add(table)

        // Add summary
        val totalAmount = orders.sumOf { it.totalPrice }
        val totalOrders = orders.size

        val summary = Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()
            .addCell(Cell().add(Paragraph("Total:")).setBold())
            .addCell(Cell().add(Paragraph(totalAmount.toString())))
            .addCell(Cell().add(Paragraph("Cantidad Pedidos:")).setBold())
            .addCell(Cell().add(Paragraph(totalOrders.toString())))

        document.add(summary)
        document.close()

        return outputStream.toByteArray()
    }
} 