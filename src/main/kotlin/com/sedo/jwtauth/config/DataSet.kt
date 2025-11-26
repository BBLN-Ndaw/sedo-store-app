package com.sedo.jwtauth.config

import Product
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderItem
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.PaymentMethod
import com.sedo.jwtauth.model.entity.PaymentStatus
import com.sedo.jwtauth.model.entity.Supplier
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

val order1Id = "110f8e1ab9ee73091c8ba635"
val order2Id = "220f8e1ab9ee73091c8ba635"
val order3Id = "330f8e1ab9ee73091c8ba635"
val order4Id = "440f8e1ab9ee73091c8ba635"
val order5Id = "550f8e1ab9ee73091c8ba635"

val supplierId1 = "890f8e1ab9ee73091c8ba635"
val supplierId2 = "990f8e1ab9ee73091c8ba635"

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
        imageUrls = listOf("https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400"),
        isActive = true,
        isOnPromotion = true,
        promotionPrice = BigDecimal("699.99"),
        promotionEndDate = Instant.parse("2025-08-20T00:00:00Z")
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
            imageUrls = listOf("products/ordinateur_portable_gaming/HP-Victus-16-r0000sf-1_1764088852804_bf5b3f20.jpg",
                               "products/ordinateur_portable_gaming/PC-gaming-msi-sword-16-1403x935_1764088852899_d684cb94.jpg",
                               "products/ordinateur_portable_gaming/lenovo_ordinateur_1764088978768_05060778.jpg",
                               "products/ordinateur_portable_gaming/PC-gaming-msi-sword-16-1403x935_1764091548454_ba4ea1e4.jpg",
                               "products/ordinateur_portable_gaming/HP-Victus-16-r0000sf-1_1764091548557_3542a3ce.jpg"),
            isActive = true
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
            imageUrls = listOf("products/casque_audio_bluetooth/casque_2_1764091716523_e1cfc07c.jpg",
                               "products/casque_audio_bluetooth/jbl-tune-510-bt-casque-supra-auriculaire-sans-fi_1764091716615_bb3a9c27.webp",
                               "products/casque_audio_bluetooth/casque_1764091716577_7f9dbed3.jpg"),
            isActive = true,
            isOnPromotion = true,
            promotionPrice = BigDecimal("179.99"),
            promotionEndDate = Instant.parse("2025-08-16T00:00:00Z")
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
            imageUrls = listOf("products/montre_connectée_sport/montre-connectee-homme-femmes-intelligente-sport-e_1764091846426_e903f5f7.webp",
                               "products/montre_connectée_sport/Platyne-montre-connectee_1764091846586_8671db1b.webp",
                               "products/montre_connectée_sport/montre-connectee-smarty-collection-level-en-metal-chrome_1764091846689_1989d3f8.jpg"),
            isActive = true
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
            imageUrls = listOf("products/tablette_graphique_pro/tablette_2_1764092017306_bca2717c.jpg",
                               "products/tablette_graphique_pro/tablette_3_1764092017225_4c926a2f.jpg",
                               "products/tablette_graphique_pro/tablette_1_1764092017173_ddf12805.jpg"),
            isActive = true,
            isOnPromotion = true,
            promotionPrice = BigDecimal("329.99"),
            promotionEndDate = Instant.parse("2025-08-25T00:00:00Z"),
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
            imageUrls = listOf("products/caméra_action_4k/camera_3_1764092231476_cef79a20.jpg",
                               "products/caméra_action_4k/camera_2_1764092231544_a16c8fe5.jpg",
                               "products/caméra_action_4k/camera_1_1764092231515_c401e5c0.jpg"),
            isActive = true,
            isOnPromotion = true,
            promotionPrice = BigDecimal("149.99"),
            promotionEndDate = Instant.parse("2025-08-18T00:00:00Z")
    ))

val mockOrders = listOf(
    Order(
        order1Id,
        orderNumber = "CMD-20250819-0001",
        customerUserName = "customer",
        status = OrderStatus.PENDING,
        subtotal = BigDecimal("1757.00"),
        shippingAmount = BigDecimal("5.90"),
        taxAmount = BigDecimal("350.00"),
        totalAmount = BigDecimal("2112.90"),
        shippingAddress = Address("10 rue de Paris", "Lyon", "69001", "FR"),
        billingAddress = Address("10 rue de Paris", "Lyon", "69001", "FR"),
        estimatedDeliveryDate = Instant.parse("2025-08-25T10:00:00Z"),
        notes = "Livrer avant midi",
        items = listOf(
            OrderItem(smartPhoneProdtucId, "Smartphone Premium X1", 1, productUnitPrice = BigDecimal("899.99")),
            OrderItem(casqueProdtucId, "Casque Audio Bluetooth", 2, productUnitPrice = BigDecimal("249.99"))
        ),
        paymentMethod = PaymentMethod.CREDIT_CARD,
        paymentStatus = PaymentStatus.PENDING,
        processedByUser = "SYSTEM"
    ),
    Order(
        order2Id,
        orderNumber = "CMD-20250819-0002",
        customerUserName = "customer",
        status = OrderStatus.CONFIRMED,
        subtotal = BigDecimal("1299.99"),
        shippingAmount = BigDecimal("0.00"),
        taxAmount = BigDecimal("260.00"),
        totalAmount = BigDecimal("1559.99"),
        shippingAddress = Address("22 boulevard Saint-Michel", "Paris", "75005", "FR"),
        billingAddress = null,
        estimatedDeliveryDate = Instant.parse("2025-08-22T14:00:00Z"),
        notes = "Cadeau, emballage soigné",
        items = listOf(
            OrderItem(ordinateurProdtucId, "Ordinateur Portable Gaming", 1, productUnitPrice = BigDecimal("1299.99"))
        ),
        paymentMethod = PaymentMethod.PAYPAL,
        paymentStatus = PaymentStatus.COMPLETED,
        processedByUser = "Sophie Martin",
    ),
    Order(
        order3Id,
        orderNumber = "CMD-20250819-0003",
        customerUserName = "customer",
        status = OrderStatus.PROCESSING,
        subtotal = BigDecimal("329.99"),
        shippingAmount = BigDecimal("4.90"),
        taxAmount = BigDecimal("66.00"),
        totalAmount = BigDecimal("400.89"),
        shippingAddress = Address("5 avenue Habib Bourguiba", "Tunis", "1000", "TN"),
        billingAddress = Address("5 avenue Habib Bourguiba", "Tunis", "1000", "TN"),
        estimatedDeliveryDate = Instant.parse("2025-08-28T16:30:00Z"),
        notes = null,
        items = listOf(
            OrderItem(montreProdtucId, "Montre Connectée Sport", 1, productUnitPrice = BigDecimal("329.99"))
        ),
        paymentMethod = PaymentMethod.BANK_TRANSFER,
        paymentStatus = PaymentStatus.PENDING,
        processedByUser = "Karim Ben Salah",
    ),
    Order(
        order4Id,
        orderNumber = "CMD-20250819-0004",
        customerUserName = "customer",
        status = OrderStatus.SHIPPED,
        subtotal = BigDecimal("449.99"),
        shippingAmount = BigDecimal("15.00"),
        taxAmount = BigDecimal("90.00"),
        totalAmount = BigDecimal("554.99"),
        shippingAddress = Address("Via Roma 12", "Milan", "20100", "IT"),
        billingAddress = null,
        estimatedDeliveryDate = Instant.parse("2025-08-24T09:00:00Z"),
        notes = "Expédition express DHL",
        items = listOf(
            OrderItem(tabletteProdtucId, "Tablette Graphique Pro", 1, productUnitPrice =  BigDecimal("449.99"))
        ),
        paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
        paymentStatus = PaymentStatus.PENDING,
        processedByUser = "Marco Bianchi",
    ),
    Order(
        order5Id,
        orderNumber = "CMD-20250819-0005",
        customerUserName = "customer",
        status = OrderStatus.DELIVERED,
        subtotal = BigDecimal("199.99"),
        shippingAmount = BigDecimal("0.00"),
        taxAmount = BigDecimal("40.00"),
        totalAmount = BigDecimal("239.99"),
        shippingAddress = Address("221B Baker Street", "London", "NW16XE", "UK"),
        billingAddress = Address("221B Baker Street", "London", "NW16XE", "UK"),
        estimatedDeliveryDate = Instant.parse("2025-08-20T11:00:00Z"),
        notes = "Remis en main propre",
        items = listOf(
            OrderItem(cameraProdtucId, "Caméra Action 4K", 1, productUnitPrice = BigDecimal("199.99"))
        ),
        paymentMethod = PaymentMethod.CREDIT_CARD,
        paymentStatus = PaymentStatus.COMPLETED,
        processedByUser = "Emma Johnson",
    )
)

val mockSuppliers = listOf(
        Supplier(
            id = supplierId1,
            name = "Tech Distributors Inc.",
            contactPersonName = "Jean philippe",
            category = catAudioId,
            email = "tech.distrib]gmail.com",
            phone = "+1-555-1234",
            address = Address(
                street = "123 Tech Avenue",
                city = "San Francisco",
                postalCode = "94107",
                country = "USA"
            ),
            isActive = true
        ),
        Supplier(
            id = supplierId2,
            name = "Global Electronics Ltd.",
            contactPersonName = "Alice Dupont",
            category = catElectroniqueId,
            email = "global.electonic@gmail.com",
            phone = "+44-20-7946-0958",
            address = Address(
                street = "456 Innovation Road",
                city = "London",
                postalCode = "EC1A 1BB",
                country = "UK"
            ),
            isActive = true
        ))