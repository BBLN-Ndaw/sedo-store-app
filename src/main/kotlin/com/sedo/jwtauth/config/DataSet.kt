package com.sedo.jwtauth.config

import Product
import com.sedo.jwtauth.model.entity.Category
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.Instant

val catElectroniqueId = "689f8e1ab9ee73091c8ba684"
val catAudioId = "689f8e1ab9ee73091c8ba685"
val catWearablesId = "689f8e1ab9ee73091c8ba686"
val catPhotoVideoId = "689f8e1ab9ee73091c8ba687"
val smartPhoneProdtucId = "689f8e1ab9ee73091c8ba686"
val ordinateurProdtucId = "716f8e1ab9ee73091c8ba634"
val casqueProdtucId = "310f8e1ab9ee73091c8ba632"
val montreProdtucId = "210f8e1ab9ee73091c8ba632"
val tabletteProdtucId = "410f8e1ab9ee73091c8ba633"
val cameraProdtucId = "510f8e1ab9ee73091c8ba635"

val mockCategories = listOf(
    Category(catElectroniqueId, "Électronique", "Appareils électroniques", true),
    Category(catAudioId, "Audio", "Équipements audio", true),
    Category(catWearablesId, "Wearables", "Objets connectés portables", true),
    Category(catPhotoVideoId, "Photo/Vidéo", "Équipements photo et vidéo", true)
)

val mockProducts = listOf(
    Product(
        id = smartPhoneProdtucId,
        name = "Smartphone Premium X1",
        description = "Smartphone haut de gamme avec écran OLED 6.5\" et triple caméra 108MP",
        sku = "PHONE-X1-001",
        categoryId = catElectroniqueId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("899.99"),
        purchasePrice = BigDecimal("650.00"),
        stockQuantity = 25,
        minStock = 5,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400"),
        isActive = true,
        isOnPromotion = true,
        promotionPrice = BigDecimal("699.99"),
        promotionEndDate = Instant.parse("2025-08-20T00:00:00Z"),
        createdAt = Instant.parse("2024-01-15T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-01T00:00:00Z")
    ),
    Product(
        id = ordinateurProdtucId,
        name = "Ordinateur Portable Gaming",
        description = "PC portable gaming avec RTX 4070, Intel i7 et 16GB RAM",
        sku = "LAPTOP-GAM-002",
        categoryId = catElectroniqueId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("1299.99"),
        purchasePrice = BigDecimal("950.00"),
        stockQuantity = 12,
        minStock = 3,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400"),
        isActive = true,
        createdAt = Instant.parse("2024-01-20T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-05T00:00:00Z")
    ),
    Product(
        id = casqueProdtucId,
        name = "Casque Audio Bluetooth",
        description = "Casque sans fil avec réduction de bruit active et autonomie 30h",
        sku = "AUDIO-BT-003",
        categoryId = catAudioId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("249.99"),
        purchasePrice = BigDecimal("180.00"),
        stockQuantity = 45,
        minStock = 10,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"),
        isActive = true,
        isOnPromotion = true,
        promotionPrice = BigDecimal("179.99"),
        promotionEndDate = Instant.parse("2025-08-16T00:00:00Z"),
        createdAt = Instant.parse("2024-01-25T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-10T00:00:00Z")
    ),
    Product(
        id = montreProdtucId,
        name = "Montre Connectée Sport",
        description = "Montre connectée étanche avec GPS et suivi de santé avancé",
        sku = "WATCH-SPT-004",
        categoryId = catWearablesId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("329.99"),
        purchasePrice = BigDecimal("240.00"),
        stockQuantity = 18,
        minStock = 5,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"),
        isActive = true,
        createdAt = Instant.parse("2024-02-01T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-15T00:00:00Z")
    ),
    Product(
        id = tabletteProdtucId,
        name = "Tablette Graphique Pro",
        description = "Tablette professionnelle pour designers avec stylet sensible à la pression",
        sku = "TAB-GRAPH-005",
        categoryId = catElectroniqueId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("449.99"),
        purchasePrice = BigDecimal("320.00"),
        stockQuantity = 8,
        minStock = 2,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400"),
        isActive = true,
        isOnPromotion = true,
        promotionPrice = BigDecimal("329.99"),
        promotionEndDate = Instant.parse("2025-08-25T00:00:00Z"),
        createdAt = Instant.parse("2024-02-05T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-20T00:00:00Z")
    ),
    Product(
        id = cameraProdtucId,
        name = "Caméra Action 4K",
        description = "Caméra d'action ultra-compacte 4K 60fps avec stabilisation",
        sku = "CAM-ACT-006",
        categoryId = catPhotoVideoId,
        supplierId = ObjectId().toString(),
        sellingPrice = BigDecimal("199.99"),
        purchasePrice = BigDecimal("140.00"),
        stockQuantity = 32,
        minStock = 8,
        unit = "pièce",
        expirationDate = Instant.parse("2025-12-31T00:00:00Z"),
        images = listOf("https://images.unsplash.com/photo-1606983340126-99ab4feaa64a?w=400"),
        isActive = true,
        isOnPromotion = true,
        promotionPrice = BigDecimal("149.99"),
        promotionEndDate = Instant.parse("2025-08-18T00:00:00Z"),
        createdAt = Instant.parse("2024-02-10T00:00:00Z"),
        updatedAt = Instant.parse("2024-02-25T00:00:00Z")
    ))