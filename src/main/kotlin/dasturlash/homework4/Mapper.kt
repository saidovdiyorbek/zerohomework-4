package dasturlash.homework4

import org.springframework.stereotype.Component

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
                order = order,
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
                order,
                description
            )
        }
    }
}