package com.kotlin.orders.controller

import com.kotlin.orders.service.DashboardService
import com.kotlin.orders.service.ReportService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
class DashboardController(
    private val dashboardService: DashboardService,
    private val reportService: ReportService
) {

    @GetMapping("/truck-sales/daily")
    fun getDailyTruckSales(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ) = dashboardService.getDailyTruckSales(date)

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

    @GetMapping("/truck-delivery-report")
    fun getTruckDeliveryReport(
        @RequestParam truckId: Int,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<Map<String, Any>> {
        val report = reportService.getTruckDeliveryReport(truckId, startDate, endDate)
        return ResponseEntity.ok(report)
    }
} 