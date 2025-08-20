package com.sedo.jwtauth.controller

import com.sedo.jwtauth.service.OrderService
import com.sedo.jwtauth.service.ProductService
import com.sedo.jwtauth.service.SaleService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val saleService: SaleService,
    private val orderService: OrderService,
    private val productService: ProductService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getDashboard(): ResponseEntity<Map<String, Any>> {
        val today = LocalDate.now()
        
        // Ventes du jour
        val todaysSales = saleService.getTodaysSales()
        val todaysRevenue = saleService.getDailySalesTotal(today)
        
        // Commandes
        val orderStats = orderService.getOrderStats()
        val pendingOrders = orderService.getPendingOrders()
        val readyForPickupOrders = orderService.getReadyForPickupOrders()
        
        // Produits
        val lowStockProducts = productService.getLowStockProducts()
        val expiredProducts = productService.getExpiredProducts()
        val expiringProducts = productService.getProductsExpiringIn(7)
        
        // Top produits
        val topProducts = saleService.getTopSellingProducts(5)
        
        val dashboard = mapOf(
            "sales" to mapOf(
                "today" to mapOf(
                    "count" to todaysSales.size,
                    "revenue" to todaysRevenue
                ),
                "topProducts" to topProducts
            ),
            "orders" to mapOf(
                "pending" to pendingOrders.size,
                "readyForPickup" to readyForPickupOrders.size,
                "stats" to orderStats
            ),
            "inventory" to mapOf(
                "lowStock" to mapOf(
                    "count" to lowStockProducts.size,
                    "products" to lowStockProducts.take(5).map { 
                        mapOf(
                            "id" to it.id,
                            "name" to it.name,
                            "stock" to it.stockQuantity,
                            "minLevel" to it.minStock
                        )
                    }
                ),
                "expired" to mapOf(
                    "count" to expiredProducts.size,
                    "products" to expiredProducts.take(5).map {
                        mapOf(
                            "id" to it.id,
                            "name" to it.name,
                            "expirationDate" to it.expirationDate
                        )
                    }
                ),
                "expiring" to mapOf(
                    "count" to expiringProducts.size,
                    "products" to expiringProducts.take(5).map {
                        mapOf(
                            "id" to it.id,
                            "name" to it.name,
                            "expirationDate" to it.expirationDate
                        )
                    }
                )
            ),
            "alerts" to listOf<Map<String, Any>>().plus(
                if (lowStockProducts.isNotEmpty()) {
                    listOf(mapOf(
                        "type" to "LOW_STOCK",
                        "message" to "${lowStockProducts.size} produit(s) en rupture de stock",
                        "severity" to "warning"
                    ))
                } else emptyList()
            ).plus(
                if (expiredProducts.isNotEmpty()) {
                    listOf(mapOf(
                        "type" to "EXPIRED",
                        "message" to "${expiredProducts.size} produit(s) expiré(s)",
                        "severity" to "error"
                    ))
                } else emptyList()
            ).plus(
                if (expiringProducts.isNotEmpty()) {
                    listOf(mapOf(
                        "type" to "EXPIRING",
                        "message" to "${expiringProducts.size} produit(s) expirent bientôt",
                        "severity" to "info"
                    ))
                } else emptyList()
            )
        )
        
        return ResponseEntity.ok(dashboard)
    }
    
    @GetMapping("/sales-today")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getSalesToday(): ResponseEntity<Map<String, Any>> {
        val todaysSales = saleService.getTodaysSales()
        val todaysRevenue = saleService.getDailySalesTotal(LocalDate.now())
        
        return ResponseEntity.ok(mapOf(
            "date" to LocalDate.now(),
            "salesCount" to todaysSales.size,
            "revenue" to todaysRevenue,
            "sales" to todaysSales
        ))
    }
    
//    @GetMapping("/orders-summary")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
//    fun getOrdersSummary(): ResponseEntity<Map<String, Any>> {
//        val stats = orderService.getOrderStats()
//        val pending = orderService.getPendingOrders()
//        val readyForPickup = orderService.getReadyForPickupOrders()
//
//        return ResponseEntity.ok(mapOf(
//            "stats" to stats,
//            "pending" to pending.map {
//                mapOf(
//                    "id" to it.id,
//                    "orderNumber" to it.orderNumber,
//                    "customerName" to it.customerName,
//                    "totalAmount" to it.totalAmount,
//                    "orderDate" to it.createdAt
//                )
//            },
//            "readyForPickup" to readyForPickup.map {
//                mapOf(
//                    "id" to it.id,
//                    "orderNumber" to it.orderNumber,
//                    "customerName" to it.customerName,
//                    "totalAmount" to it.totalAmount,
//                    "readyAt" to it.readyAt
//                )
//            }
//        ))
//    }
//
    @GetMapping("/inventory-alerts")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getInventoryAlerts(): ResponseEntity<Map<String, Any>> {
        val lowStockProducts = productService.getLowStockProducts()
        val expiredProducts = productService.getExpiredProducts()
        val expiringProducts = productService.getProductsExpiringIn(7)
        
        return ResponseEntity.ok(mapOf(
            "lowStock" to lowStockProducts,
            "expired" to expiredProducts,
            "expiring" to expiringProducts,
            "summary" to mapOf(
                "lowStockCount" to lowStockProducts.size,
                "expiredCount" to expiredProducts.size,
                "expiringCount" to expiringProducts.size
            )
        ))
    }
}
