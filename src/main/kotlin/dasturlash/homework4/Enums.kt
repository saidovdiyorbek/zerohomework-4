package dasturlash.homework4

enum class OrderStatus {
    PENDING, DELIVERED, FINISHED, CANCELLED
}
enum class PaymentMethod{
    UZCARD, HUMO, PAYME, CASH
}

enum class UserRole{
    ROLE_ADMIN, ROLE_USER
}

enum class PaymentStatus{
    FINISHED, CANCELLED, IN_PROGRESS
}

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    USERNAME_ALREADY_EXISTS(101),
    PRODUCT_NOT_FOUND(200),
    CATEGORY_NOT_FOUND(300),
    INSUFFICIENT_FUNDS(400),
    USERNAME_NOT_FOUND(102),

}
