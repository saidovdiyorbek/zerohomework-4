package dasturlash.homework4

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
    val order: Long,
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
    val order: Long?,
    val description: String?
)

data class CategoryUpdateRequest(
    val name: String?,
    val order: Long?,
    val description: String?
)

data class ProductCreateRequest(
    val name: String,
    val count: Long,
    val categoryId: Long,
    val prince: BigDecimal
)

data class ProductFullInfo(
    val id: Long?,
    val createdDate: Date?,
    val lastModifiedDate: Date?,
    val createdBy: String?,
    val lastModifiedBy: String?,
    val deleted: Boolean?,
    val name: String?,
    val count: Long?,
    val categoryId: Long?,
    var price: BigDecimal?,
)

data class ProductUpdateRequest(
    val name: String?,
    val count: Long?,
    val categoryId: Long?,
    val prince: BigDecimal?,
)