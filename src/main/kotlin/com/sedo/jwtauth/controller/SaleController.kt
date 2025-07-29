package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.dto.SaleDto
import com.sedo.jwtauth.model.entity.Sale
import com.sedo.jwtauth.service.SaleService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/sales")
class SaleController(
    private val saleService: SaleService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getAllSales(): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleService.getAllSales())
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getSaleById(@PathVariable id: String): ResponseEntity<Sale> {
        return ResponseEntity.ok(saleService.getSaleById(id))
    }
    
    @GetMapping("/today")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getTodaysSales(): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleService.getTodaysSales())
    }
    
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getSalesByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleService.getSalesByDate(date))
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getSalesByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleService.getSalesByDateRange(startDate, endDate))
    }
    
    @GetMapping("/daily-total/{date}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getDailySalesTotal(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<Map<String, Any>> {
        val total = saleService.getDailySalesTotal(date)
        return ResponseEntity.ok(mapOf(
            "date" to date,
            "total" to total
        ))
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun createSale(@Valid @RequestBody saleDto: SaleDto): ResponseEntity<Sale> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(saleService.createSale(saleDto))
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getSalesStats(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(saleService.getSalesStats(startDate, endDate))
    }
    
    @GetMapping("/top-products")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getTopSellingProducts(@RequestParam(defaultValue = "10") limit: Int): ResponseEntity<List<Map<String, Any>>> {
        return ResponseEntity.ok(saleService.getTopSellingProducts(limit))
    }
}
