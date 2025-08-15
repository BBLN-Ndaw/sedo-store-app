import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
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
    val sellingPrice: BigDecimal,
    val purchasePrice: BigDecimal,
    val stockQuantity: Int,
    val minStock: Int,
    val unit: String,
    val expirationDate: Instant,
    val images: List<String> = emptyList(),
    val isActive: Boolean = true,
    val isOnPromotion: Boolean = false,
    val promotionPrice: BigDecimal? = null,
    val promotionEndDate: Instant? = null,
    @field:CreatedDate
    val createdAt: Instant? = null,
    @field:LastModifiedDate
    val updatedAt: Instant? = null,
)
