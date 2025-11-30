package dasturlash.homework4

import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.math.log
import kotlin.text.category

//auth service
interface AuthService {
    fun login(loginRequest: LoginRequest, request: HttpServletRequest): BaseMessage

}
@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val customUserDetailsService: CustomUserDetailsService
) : AuthService {
    override fun login(loginRequest: LoginRequest, request: HttpServletRequest): BaseMessage {


        val userDetails: UserDetails? = customUserDetailsService.loadUserByUsername(loginRequest.username)

        val authentication =  authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password)
            )

        SecurityContextHolder.getContext().authentication = authentication
        println(SecurityContextHolder.getContext().authentication.name)
        return BaseMessage.OK
    }

}
//auth service

//user service
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
                status = UserStatus.ACTIVE,
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

//Product service
interface ProductService{
    fun create(request: ProductCreateRequest): Any
    fun getAll(): List<ProductFullInfo>
    fun getOne(id: Long): ProductFullInfo
    fun update(id: Long, updateBody: ProductUpdateRequest)
    fun delete(id: Long)
}
@Service
class ProductServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val repository: ProductRepository,
    private val mapper: ProductMapper,
) : ProductService {

    override fun create(request: ProductCreateRequest){
        request.run {
            val category = categoryRepository.findByIdAndDeletedFalse(request.categoryId) ?:
            throw CategoryNotFoundException()
            repository.save(mapper.toEntity(request, category))
        }
    }

    override fun getAll(): List<ProductFullInfo> {
        val response: MutableList<ProductFullInfo> = mutableListOf()
        repository.findAll().forEach {product ->
            mapper.toProductFullInfo(product).run {
                response.add(this)
            }
        }
        return response
    }

    override fun getOne(id: Long): ProductFullInfo {
        val product = repository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()

        return product.run {
            mapper.toProductFullInfo(product)
        }

    }

    override fun update(id: Long, updateBody: ProductUpdateRequest) {
        val product = repository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()
        updateBody.run {
            this.categoryId?.let {
                val category =  categoryRepository.findByIdAndDeletedFalse(this.categoryId) ?: throw CategoryNotFoundException()
                product.category = category
            }
            this.name?.let { product.name = name}
            this.stockCount?.let { product.stockCount = stockCount}
            this.prince?.let { product.price = prince}
        }
        repository.save(product)
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw ProductNotFoundException()
    }

}
//Product service

//Order service
interface OrderService{
    fun create(orderRequest: OrderCreateRequest)
}

@Service
class OrderServiceImpl(
    private val repository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val sessionUser: SecurityUtils,
) : OrderService {
    @Transactional
    override fun create(orderRequest: OrderCreateRequest) {


        val user = userRepository.findByIdAndDeletedFalse(sessionUser.getCurrentUserId()) ?: throw UserNotFoundException()
        var saveOrder: Order? = null

        var orderItemsCreation: MutableList<OrderItem> = mutableListOf()
        var calculatedTotalAmount = BigDecimal.ZERO
        var create: Boolean = false
        var forCount = 1
        orderRequest.orderItems.forEach { item ->

            val product = productRepository.findByIdAndDeletedFalse(item.productId)
                    ?: throw ProductNotFoundException()
            val itemAmountTotal = product.price.multiply(BigDecimal(item.quantity))
            calculatedTotalAmount = calculatedTotalAmount.add(itemAmountTotal)
            forCount++

            if (create){
                saveOrder = repository.save(Order(
                    user = user,
                    status = OrderStatus.PENDING,
                    totalAmount = calculatedTotalAmount,
                ))
            }

            if (create){
                orderRequest.orderItems.forEach { item ->
                    orderItemsCreation.add(OrderItem(
                        order = saveOrder,
                        product = product,
                        item.quantity,
                        product.price,
                        itemAmountTotal
                    ))
                }
                orderItemRepository.saveAll(orderItemsCreation)
            }

            if (forCount == orderRequest.orderItems.size) {
                create = true
            }
        }
        paymentRepository.save(Payment(
            order = saveOrder,
            user = user,
            paymentMethod = orderRequest.payment.paymentMethod,
            amount = calculatedTotalAmount,
        ))

    }
}
//Order service