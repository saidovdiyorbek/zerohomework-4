package dasturlash.homework4

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtils {

    fun getCurrentUser(): CustomUserDetails? {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as CustomUserDetails?
    }

    fun getCurrentUserId(): Long{
        return getCurrentUser()?.id
            ?: throw UserNotAuthenticatedException()
    }

    fun getCurrentUserName(): String {
        return getCurrentUser()?.username
            ?: throw UserNotAuthenticatedException()
    }

    fun getCurrentUserRole(): UserRole {
        return getCurrentUser()?.role
            ?: throw RuntimeException("User not authenticated")
    }

    fun isAdmin(): Boolean {
        return getCurrentUser()?.role == UserRole.ROLE_ADMIN
    }

    fun isUserActive(): Boolean {
        return getCurrentUser()?.status == UserStatus.ACTIVE
    }
}