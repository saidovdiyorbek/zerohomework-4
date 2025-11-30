package dasturlash.homework4

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
    ) : UserDetailsService{
    override fun loadUserByUsername(username: String?): UserDetails? {
        val userEntity = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException()
        println(SecurityContextHolder.getContext().authentication)
        return CustomUserDetails(userEntity)
    }


}

