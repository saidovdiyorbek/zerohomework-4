package dasturlash.homework4

import org.springframework.stereotype.Component
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
