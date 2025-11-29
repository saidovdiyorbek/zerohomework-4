package dasturlash.homework4

import org.springframework.stereotype.Service

interface UserService{
    fun create(userRequest: UserRequest)
    fun getOne(id: Long): UserFullInfo
    fun delete(id: Long)
    fun getAll(): List<UserFullInfo>
    fun update(id: Long, updateBody: UpdateUserRequest)
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val mapper: UserMapper,
) : UserService {
    override fun create(userRequest: UserRequest) {
        if(userRepository.existsByUsername(userRequest.username)){
            throw UserAlreadyExistsException()
        }

        userRepository.save(User(
            fullname = userRequest.fullname,
            username = userRequest.username,
            role = UserRole.USER,
            email = userRequest.email,
            address = userRequest.address,))
    }

    override fun getOne(id: Long): UserFullInfo {
        return userRepository.findByIdAndDeletedFalse(id)?.let {
            mapper.toUserFullInfo(it)
        } ?: throw UserNotFoundException()
    }
    override fun delete(id: Long) {
        userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
        userRepository.trash(id)
    }

    override fun getAll(): List<UserFullInfo> {
        val responseUsers: MutableList<UserFullInfo> = mutableListOf()

        userRepository.findAll().forEach {
            responseUsers.add(UserFullInfo(it.fullname, it.username, it.balance, it.createdDate))
        }
        return responseUsers
    }

    override fun update(id: Long, updateBody: UpdateUserRequest) {
        updateBody.run {
            val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
            fullname?.let { user.fullname = it }
            balance?.let { user.balance = it }
            username?.let { newUsername ->
                userRepository.existsByUsername(newUsername)
            }

        }
    }

    override fun getAllPayments(id: Long): List<UserPaymentTransactionResponse> {
        userRepository.findById(id).getOrThrowNotFound(UserNotFoundException())
        val userPaymentsByUserId = userPaymentRepository.getUserPaymentsByUserId(id)
        val response: MutableList<UserPaymentTransactionResponse> = mutableListOf()

        userPaymentsByUserId?.run {
            for (payments in this) {
                response.add(UserPaymentTransactionResponse(
                    payments?.id,
                    id,
                    payments?.createdDate,
                    payments?.amount
                ))
            }
        }
        return response;
    }
}
