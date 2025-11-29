package dasturlash.homework4

import org.springframework.stereotype.Component
import java.util.Date
import kotlin.Long

@Component
class UserMapper {

    fun toUserFullInfo(user: User): UserFullInfo {
        user.run {
            return UserFullInfo(
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