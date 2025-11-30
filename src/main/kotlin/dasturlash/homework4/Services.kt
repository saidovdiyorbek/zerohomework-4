package dasturlash.homework4

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserService{
    fun create(userRequest: UserRequest)
    fun getOneAdmin(id: Long): UserFullInfoAdmin
    fun delete(id: Long)
    fun getAllAdmin(): List<UserFullInfoAdmin>
    fun update(id: Long, updateBody: UpdateUserRequest)
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val mapper: UserMapper,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    override fun create(userRequest: UserRequest) {
        if (userRepository.existsByUsername(userRequest.username)) {
            throw UserAlreadyExistsException()
        }

        userRepository.save(
            User(
                fullname = userRequest.fullname,
                username = userRequest.username,
                role = UserRole.ROLE_USER,
                email = userRequest.email,
                password = passwordEncoder.encode(userRequest.password),
                address = userRequest.address,
            )
        )
    }

    override fun getOneAdmin(id: Long): UserFullInfoAdmin {
        return userRepository.findByIdAndDeletedFalse(id)?.let {
            mapper.toUserFullInfoAdmin(it)
        } ?: throw UserNotFoundException()
    }

    override fun delete(id: Long) {
        userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
        userRepository.trash(id)
    }

    override fun getAllAdmin(): List<UserFullInfoAdmin>{
        val responseUsers: MutableList<UserFullInfoAdmin> = mutableListOf()

        userRepository.findAll().forEach {
            responseUsers.add(
                mapper.toUserFullInfoAdmin(it)
            )
        }
        return responseUsers
    }

    override fun update(id: Long, updateBody: UpdateUserRequest) {
        updateBody.run {
            val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()

            fullname?.let { user.fullname = it }
            username?.let { newUsername ->
                userRepository.existsByUsername(newUsername)
            }

        }
    }
}
//user service

//category service
interface CategoryService{
    fun create(request: CategoryCreateRequest)
    fun getAll(): List<CategoryFullInfo>
    fun getOne(id: Long): CategoryFullInfo
    fun update(id: Long, updateBody: CategoryUpdateRequest)
    fun delete(id: Long)

}

@Service
class CategoryServiceImpl(
    private val repository: CategoryRepository,
    private val mapper: CategoryMapper
) : CategoryService {
    override fun create(request: CategoryCreateRequest) {
        request.run {
            repository.findByName(name)?.let {
                throw CategoryNotFoundException()
            } ?: run {
                repository.shiftOrderUp(this.order)
                repository.save(mapper.toEntity(this))
            }
        }
    }

    override fun getAll(): List<CategoryFullInfo> {
        val responses: MutableList<CategoryFullInfo> = mutableListOf()
        repository.findAll().forEach { category ->
            responses.add(mapper.toCategoryFullInfo(category))
        }
        return responses
    }

    override fun getOne(id: Long): CategoryFullInfo {
        val category = repository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException()

        return category.run {
            mapper.toCategoryFullInfo(this)
        }
    }

    override fun update(id: Long, updateBody: CategoryUpdateRequest) {
        val category = repository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException()

        repository.save(updateBody.run {
            this.name?.let { category.name = name }
            this.description?.let { category.description = description }
            category
        })
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw CategoryNotFoundException()
    }
}
//category service

