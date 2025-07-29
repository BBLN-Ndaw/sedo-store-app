package com.sedo.jwtauth.constants

/**
 * Application constants
 */
object Constants {
    
    object Endpoints {
        const val API = "/api"
        const val LOGIN = "/login"
        const val HELLO = "/hello"
        const val ADMIN = "/admin"
        const val EMPLOYEE = "/employee"
        const val USER = "$API/users"
        const val CATEGORIES = "$API/categories"
        const val SUPPLIERS = "$API/suppliers"
        const val PRODUCTS = "$API/products"
        const val ORDERS = "$API/orders"
        const val SALES = "$API/sales"
        const val AUDIT = "$API/audit"
        const val DASHBOARD = "$API/dashboard"
    }
    
    object Roles {
        const val OWNER = "OWNER"        // Propriétaire - accès total
        const val EMPLOYEE = "EMPLOYEE"  // Employé/Gestionnaire - gestion quotidienne
        const val CLIENT = "CLIENT"      // Client - commandes uniquement
    }
    
    object TaxRate {
        const val DEFAULT_TAX_RATE = 0.20 // 20% TVA en France
    }
}