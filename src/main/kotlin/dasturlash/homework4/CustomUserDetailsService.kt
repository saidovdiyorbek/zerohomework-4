package dasturlash.homework4

import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,

) : UserDetail{


}