package com.kotlin.orders.controller

import com.kotlin.orders.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/reports")
@CrossOrigin
class ReportController(private val reportService: ReportService) {

    @GetMapping("/truck-delivery")
    fun generateTruckDeliveryReport(
        @RequestParam truckId: Int,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<ByteArray> {
        val pdfBytes = reportService.generateTruckDeliveryReport(truckId, startDate, endDate)
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=truck-delivery-report.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }
} 