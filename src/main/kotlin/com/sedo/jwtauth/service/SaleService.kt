package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.InsufficientStockException
import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.dto.SaleDto
import com.sedo.jwtauth.model.entity.Sale
import com.sedo.jwtauth.model.entity.SaleItem
import com.sedo.jwtauth.repository.SaleRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId

@Service
class SaleService(
    private val saleRepository: SaleRepository,
    private val productService: ProductService,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(SaleService::class.java)
    
    fun getAllSales(): List<Sale> {
        logger.debug("Retrieving all sales")
        return saleRepository.findAll()
    }
    
    fun getSaleById(id: String): Sale {
        logger.debug("Retrieving sale with ID: {}", id)
        return saleRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Sale not found with ID: $id")
    }
    
    fun getSalesByDate(date: LocalDate): List<Sale> {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        logger.debug("Retrieving sales for date: {}", date)
        return saleRepository.findByCreatedAtBetween(startOfDay, endOfDay)
    }
    
    fun getSalesByDateRange(startDate: LocalDate, endDate: LocalDate): List<Sale> {
        val start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        logger.debug("Retrieving sales between {} and {}", startDate, endDate)
        return saleRepository.findByCreatedAtBetween(start, end)
    }
    
    fun getTodaysSales(): List<Sale> {
        return getSalesByDate(LocalDate.now())
    }
    
    fun getDailySalesTotal(date: LocalDate): BigDecimal {
        return getSalesByDate(date).sumOf { it.totalAmount }
    }
    
    fun createSale(saleDto: SaleDto): Sale {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating sale with {} items by user: {}", saleDto.items.size, currentUser)
        
        // Vérifier le stock pour tous les articles
        val saleItems = mutableListOf<SaleItem>()
        var subtotal = BigDecimal.ZERO
        
        for (itemDto in saleDto.items) {
            val product = productService.getProductById(itemDto.productId)
            
            // Vérifier le stock disponible
            if (product.stockQuantity < itemDto.quantity) {
                throw InsufficientStockException(
                    "Insufficient stock for product ${product.name}. Available: ${product.stockQuantity}, Requested: ${itemDto.quantity}"
                )
            }
            
            val unitPrice = itemDto.unitPrice
            val itemTotal = unitPrice.multiply(BigDecimal(itemDto.quantity))
            
            val saleItem = SaleItem(
                productId = itemDto.productId,
                productName = product.name,
                quantity = itemDto.quantity,
                unitPrice = unitPrice,
                discountAmount = itemDto.discountAmount,
                totalPrice = itemTotal
            )
            
            saleItems.add(saleItem)
            subtotal = subtotal.add(itemTotal)
        }
        
        // Calculer les taxes
        val taxAmount = subtotal.multiply(saleDto.taxRate ?: BigDecimal("0.20")) // 20% par défaut
        val totalAmount = subtotal.add(taxAmount)
        
        // Créer la vente
        val sale = Sale(
            saleNumber = generateSaleNumber(),
            customerName = saleDto.customerName,
            items = saleItems,
            subtotal = subtotal,
            discountAmount = saleDto.discountAmount,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentMethod = saleDto.paymentMethod,
            cashReceived = saleDto.cashReceived,
            changeAmount = saleDto.changeAmount,
            processedBy = currentUser
        )
        
        val savedSale = saleRepository.save(sale)
        
        // Mettre à jour le stock pour chaque article vendu
        for (itemDto in saleDto.items) {
            val product = productService.getProductById(itemDto.productId)
            val newQuantity = product.stockQuantity - itemDto.quantity
            productService.updateStock(itemDto.productId, newQuantity, "Sale #${savedSale.id}")
        }
        
        auditService.logAction(
            userName = currentUser,
            action = "CREATE",
            entityType = "Sale",
            entityId = savedSale.id,
            description = "Created sale for ${savedSale.totalAmount}€ with ${savedSale.items.size} items",
            newData = mapOf(
                "totalAmount" to savedSale.totalAmount.toString(),
                "itemCount" to savedSale.items.size.toString(),
                "paymentMethod" to savedSale.paymentMethod,
                "customerName" to (savedSale.customerName ?: "")
            )
        )
        
        logger.info("Sale created successfully: ID {} for {}€", savedSale.id, savedSale.totalAmount)
        return savedSale
    }
    
    private fun generateSaleNumber(): String {
        val timestamp = System.currentTimeMillis()
        return "SALE-$timestamp"
    }
    
    fun getSalesStats(startDate: LocalDate, endDate: LocalDate): Map<String, Any> {
        val sales = getSalesByDateRange(startDate, endDate)
        
        val totalSales = sales.size
        val totalRevenue = sales.sumOf { it.totalAmount }
        val averageSaleAmount = if (totalSales > 0) totalRevenue.divide(BigDecimal(totalSales)) else BigDecimal.ZERO
        
        val paymentMethodStats = sales.groupBy { it.paymentMethod }
            .mapValues { (_, salesList) -> 
                mapOf(
                    "count" to salesList.size,
                    "total" to salesList.sumOf { it.totalAmount }
                )
            }
        
        val dailyStats = sales.groupBy { 
            it.createdAt?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now()
        }.mapValues { (_, salesList) ->
            mapOf(
                "count" to salesList.size,
                "total" to salesList.sumOf { it.totalAmount }
            )
        }
        
        return mapOf(
            "period" to mapOf("start" to startDate, "end" to endDate),
            "totalSales" to totalSales,
            "totalRevenue" to totalRevenue,
            "averageSaleAmount" to averageSaleAmount,
            "paymentMethods" to paymentMethodStats,
            "dailyBreakdown" to dailyStats
        )
    }
    
    fun getTopSellingProducts(limit: Int = 10): List<Map<String, Any>> {
        val allSales = saleRepository.findAll()
        val productSales = mutableMapOf<String, MutableMap<String, Any>>()
        
        allSales.forEach { sale ->
            sale.items.forEach { item ->
                val productId = item.productId
                val productStats = productSales.getOrPut(productId) {
                    mutableMapOf(
                        "productId" to productId,
                        "productName" to item.productName,
                        "totalQuantitySold" to 0,
                        "totalRevenue" to BigDecimal.ZERO
                    )
                }
                
                productStats["totalQuantitySold"] = (productStats["totalQuantitySold"] as Int) + item.quantity
                productStats["totalRevenue"] = (productStats["totalRevenue"] as BigDecimal).add(item.totalPrice)
            }
        }
        
        return productSales.values
            .sortedByDescending { it["totalQuantitySold"] as Int }
            .take(limit)
            .toList()
    }
}
