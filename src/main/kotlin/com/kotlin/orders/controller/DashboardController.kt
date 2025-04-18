package com.kotlin.orders.controller

import com.kotlin.orders.service.DashboardService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
class DashboardController(private val dashboardService: DashboardService) {

    @GetMapping("/truck-sales/daily")
    fun getDailyTruckSales(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ) = dashboardService.getDailyTruckSales(date)

    @GetMapping("/truck-sales/daily/pdf")
    fun getDailyTruckSalesPDF(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<ByteArray> {
        val pdfBytes = dashboardService.generateDailyTruckSalesPDF(date)
        
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_PDF
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily-sales-${date}.pdf")
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes)
    }

    @GetMapping("/truck-sales/weekly")
    fun getWeeklyTruckSales(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate
    ) = dashboardService.getWeeklyTruckSales(startDate)

    @GetMapping("/truck-sales/monthly")
    fun getMonthlyTruckSales(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ) = dashboardService.getMonthlyTruckSales(date)

    @GetMapping("/most-used-controls")
    fun getMostUsedControls() = dashboardService.getMostUsedControls()

    @GetMapping("/statistics")
    fun getDashboardStatistics() = dashboardService.getDashboardStatistics()
} 