package dasturlash.homework4

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
    val address: String,
    var role: UserRole,
)

data class UserFullInfo(
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
