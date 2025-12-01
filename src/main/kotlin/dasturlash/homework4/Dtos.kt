package dasturlash.homework4

import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.math.BigDecimal
import java.util.Date

data class BaseMessage(val code: Int? = null, val message: String? = null){
    companion object {
        var OK = BaseMessage(code = 0, message = "OK")
    }
}

data class UserRequest(
    val username: String,
    val fullname: String,
    val email: String,
    val password: String,
    val address: String,
    var role: UserRole,
)

data class UserFullInfoAdmin(
    var id: Long? = null,
    var createdDate: Date? = null,
    var lastModifiedDate: Date? = null,
    var createdBy: String? = null,
    var lastModifiedBy: String? = null,
    var deleted: Boolean? = null,
    var username: String,
    var fullname: String,
    var email: String,
    var address: String,
    var role: UserRole,
    )

data class UpdateUserRequest(
    var username: String? = null,
    var fullname: String? = null,
    var email: String? = null,
    var address: String? = null,
    var role: UserRole? = null,
)

data class UserInfo(
    var username: String,
    var fullname: String,
    var email: String,
    var address: String,
    var role: UserRole,
)

data class CategoryCreateRequest(
    val name: String,
    val description: String,
)

data class CategoryFullInfo(
    val id: Long?,
    val createdDate: Date?,
    val lastModifiedDate: Date?,
    val createdBy: String?,
    val lastModifiedBy: String?,
    val deleted: Boolean?,
    val name: String?,
    val description: String?
)

data class CategoryUpdateRequest(
    val name: String?,
    val order: Long?,
    val description: String?
)

data class ProductCreateRequest(
    val name: String,
    val description: String,
    val stockCount: Int,
    val categoryId: Long,
    val prince: BigDecimal,
)

data class ProductFullInfo(
    val id: Long?,
    val createdDate: Date?,
    val lastModifiedDate: Date?,
    val createdBy: String?,
    val lastModifiedBy: String?,
    val deleted: Boolean?,
    val name: String?,
    val stockCount: Int?,
    val categoryId: Long?,
    var price: BigDecimal?,
)

data class ProductUpdateRequest(
    val name: String?,
    val stockCount: Int?,
    val categoryId: Long?,
    val prince: BigDecimal?,
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class OrderCreateRequest(
    var status: OrderStatus,
    var orderItems: List<OrderItemCreate>,
    var payment: PaymentCreate
)

data class OrderItemCreate(
    val productId: Long,
    val quantity: Int,
)

data class PaymentCreate(
    val paymentMethod: PaymentMethod,
)

data class OrderInfoResponse(
    val userId: Long,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val items: List<OrderItemInfoResponse>,
)

data class OrderItemInfoResponse(
    val orderId: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalAmount: BigDecimal,
)