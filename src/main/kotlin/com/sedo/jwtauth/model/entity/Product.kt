/**
 * Product entity representing inventory items in the Store Management System.
 *
 * This entity manages all product information including pricing, inventory levels,
 * supplier relationships, and promotional details. It supports comprehensive
 * e-commerce operations with tax calculations and stock management.
 *
 * Features:
 * - Complete product catalog management
 * - Multi-level pricing (purchase, selling, promotional)
 * - Automatic tax calculations (default 20% VAT)
 * - Inventory tracking with low stock alerts
 * - Image gallery support
 * - Promotion management with time-based expiration
 * - Supplier and category relationships
 * - Audit timestamps for creation and modification
 *
 * @property id Unique identifier (MongoDB ObjectId)
 * @property name Product display name
 * @property description Optional detailed product description
 * @property sku Stock Keeping Unit - unique product identifier
 * @property categoryId Reference to product category
 * @property supplierId Reference to product supplier
 * @property sellingPrice Base selling price (excluding tax)
 * @property taxRate Tax rate applied to the product (default 20% VAT)
 * @property purchasePrice Cost price from supplier
 * @property stockQuantity Current inventory level
 * @property minStock Minimum stock threshold for reorder alerts
 * @property unit Unit of measure (pieces, kg, liters, etc.)
 * @property expirationDate Optional expiration date for perishable items
 * @property images List of image URLs for product gallery
 * @property isActive Product availability status
 * @property isOnPromotion Whether product is currently on promotion
 * @property promotionPrice Special promotional price (excluding tax)
 * @property promotionEndDate When current promotion expires
 * @property createdAt Timestamp when product was created
 * @property updatedAt Timestamp when product was last modified
 * @property version Optimistic locking version field
 *
 */
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "products")
data class Product(
    @Id
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val sku: String,
    val categoryId: String,
    val supplierId: String,
    val sellingPrice: BigDecimal,//prix HT
    val taxRate: BigDecimal = BigDecimal("0.20"), //20% par défaut
    val purchasePrice: BigDecimal,
    val stockQuantity: Int,
    val minStock: Int,
    val unit: String,
    val expirationDate: Instant? = null,
    val images: List<String> = emptyList(),
    val isActive: Boolean = true,
    val isOnPromotion: Boolean = false,
    val promotionPrice: BigDecimal? = null, //prix promo HT
    val promotionEndDate: Instant? = null,
    @field:CreatedDate
    var createdAt: Instant? = null,
    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version: Long? = null //to let mongo manage auditing field
)
