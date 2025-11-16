package com.sedo.jwtauth.model.dto

import java.math.BigDecimal

data class StatDto (val dailySales: BigDecimal?, val processingOrders: Int?,
                    val productsInStock: Int, val monthlyRevenue: BigDecimal,
                    val revenuePerMonthInCurrentYear: Map<String, BigDecimal>,
                    val averageOrderValue: BigDecimal?, val monthlyCancelledOrders: Int?)