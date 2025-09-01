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
    val expirationDate: Instant,
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
