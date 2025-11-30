package dasturlash.homework4

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,

    ) : UserDetailsService{
    override fun loadUserByUsername(username: String?): UserDetails? {
        val userEntity = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException()

        return User
            .withUsername(userEntity.username)
            .password(userEntity.password)
            .roles(userEntity.role.name.removePrefix("ROLE_"))
            .build()
    }


}

