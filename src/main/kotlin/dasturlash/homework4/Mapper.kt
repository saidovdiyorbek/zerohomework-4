package dasturlash.homework4

import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.text.category

@Component
class UserMapper {

    fun toUserFullInfoAdmin(user: User): UserFullInfoAdmin {
        user.run {
            return UserFullInfoAdmin(
                id,
                createdDate,
                lastModifiedDate,
                createdBy,
                lastModifiedBy,
                deleted,
                username,
                fullname,
                email,
                address,
                role,
            )
        }
    }
}

@Component
class CategoryMapper(){
    fun toEntity(body: CategoryCreateRequest): Category {
        body.run {
            return Category(
                name = name,
                description = description,
            )
        }
    }

    fun toCategoryFullInfo(body: Category): CategoryFullInfo {
        body.run {
            return CategoryFullInfo(
                id,
                createdDate,
                lastModifiedDate,
                createdBy,
                lastModifiedBy,
                deleted,
                name,
                description
            )
        }
    }
}

@Component
class ProductMapper(

){
    fun toEntity(body: ProductCreateRequest, category: Category): Product {
        body.run {
            return Product(
                name = name,
                description = description,
                stockCount = stockCount,
                category = category,
                price = prince
            )
        }
    }

    fun toProductFullInfo(body: Product): ProductFullInfo {
        body.run {
            return ProductFullInfo(
                id,
                createdDate,
                lastModifiedDate,
                createdBy,
                lastModifiedBy,
                deleted,
                name,
                stockCount,
                category?.id,
                price
            )
        }
    }
}

@Component
class  OrderItemMapper(){

    fun toOrderItemInfoResponse(body: OrderItem): OrderItemInfoResponse {
        body.run {
            return OrderItemInfoResponse(
                order?.id,
                product.id,
                quantity,
                unitPrice,
                totalAmount
            )
        }
    }
}

@Component
class OrderMapper(){
    fun toOrderResponse(body: Order, username: String?): OrderResponse {
        body.run {
            return OrderResponse(
                username = username,
                totalAmount =  body.totalAmount
            )
        }
    }
}
//Projections
interface OrderInfoResponseProjection{
    fun getUsername(): String
    fun getTotalAmount(): BigDecimal
    fun getStatus(): OrderStatus
}

interface OrderCountMonthlyProjection{
    fun getCounts(): Int
    fun getTotalAmountMonthly(): BigDecimal
}
//Projections