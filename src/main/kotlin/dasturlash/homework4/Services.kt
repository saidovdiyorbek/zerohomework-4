package dasturlash.homework4

import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.aspectj.weaver.ast.Or
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

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
    fun getOne(orderId: Long): Any
    fun updateStatusCancelled(orderId: Long)
    fun updateStatusAdmin(orderId: Long, status: OrderStatusBody)
    fun getAllUserOrders(): List<OrderResponse>
    fun orderCountUser(month: Byte): OrderCountUser?
    fun getUserOrdersCalculatedWithDate(startDate: LocalDate, endDate: LocalDate): OrderCalculatedResponse?
}

@Service
class OrderServiceImpl(
    private val repository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val securityUtils: SecurityUtils,
    private val orderItemMapper: OrderItemMapper,
    private val mapper: OrderMapper,
) : OrderService {
    @Transactional
    override fun create(orderRequest: OrderCreateRequest) {


        val user = userRepository.findByIdAndDeletedFalse(securityUtils.getCurrentUserId()) ?: throw UserNotFoundException()
        var saveOrder: Order? = null

        var orderItemsCreation: MutableList<OrderItem> = mutableListOf()
        var calculatedTotalAmountForOrder = BigDecimal.ZERO
        var create: Boolean = false
        var forCount = 1
        orderRequest.orderItems.forEach { item ->

            val product = productRepository.findByIdAndDeletedFalse(item.productId)
                    ?: throw ProductNotFoundException()
            val itemAmountTotalForOrder = product.price.multiply(BigDecimal(item.quantity))
            calculatedTotalAmountForOrder = calculatedTotalAmountForOrder.add(itemAmountTotalForOrder)
            forCount++

            if (create){
                saveOrder = repository.save(Order(
                    user = user,
                    status = OrderStatus.PENDING,
                    totalAmount = calculatedTotalAmountForOrder,
                ))
            }

            if (create){
                orderRequest.orderItems.forEach { item ->
                    val productForPrice = productRepository.findById(item.productId).get()
                    val itemAmountTotal = productForPrice.price.multiply(BigDecimal(item.quantity))

                    orderItemsCreation.add(OrderItem(
                        order = saveOrder,
                        product = productForPrice,
                        item.quantity,
                        productForPrice.price,
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
            amount = calculatedTotalAmountForOrder,
        ))

    }

    override fun getOne(orderId: Long): Any {
        val currentUserId = securityUtils.getCurrentUserId()
        val responseItems: MutableList<OrderItemInfoResponse> = mutableListOf()
        repository.findByUserIdAndIdProjection(currentUserId, orderId)?.let { order ->
            val findAllByOrderId = orderItemRepository.findAllByOrderId(orderId)

            findAllByOrderId.forEach {item ->
                responseItems.add(orderItemMapper.toOrderItemInfoResponse(item))
            }

            return OrderInfoResponse(
                order.getUsername(),
                order.getTotalAmount(),
                order.getStatus(),
                responseItems
            )
        }
        return responseItems
    }

    override fun updateStatusCancelled(orderId: Long) {
        repository.findByUserIdAndId(securityUtils.getCurrentUserId(), orderId)?.let { order ->
            order.status = OrderStatus.CANCELLED
            repository.save(order)
        }
    }

    override fun updateStatusAdmin(orderId: Long, status: OrderStatusBody) {
        repository.findByIdAndDeletedFalse(orderId).let {order ->
            order ?: throw OrderNotFoundException()
            if (status.orderStatus == OrderStatus.CANCELLED){
                throw AccessDeniedException()
            }
            order.status = status.orderStatus
            repository.save(order)
        }
    }

    override fun getAllUserOrders(): List<OrderResponse> {
        val findById = userRepository.findById(securityUtils.getCurrentUserId()).get()
        val responses: MutableList<OrderResponse> = mutableListOf()

        repository.findAllByUserId(securityUtils.getCurrentUserId()).run {
            this.forEach { order ->
                responses.add(mapper.toOrderResponse(order, findById.username))
            }
        }
        return responses
    }

    override fun orderCountUser(month: Byte): OrderCountUser? {
        repository.findUserMonthlyOrdered(securityUtils.getCurrentUserId(), month)?.run {
            return OrderCountUser(
                getCounts(),
                getTotalAmountMonthly()
            )
        }
        return OrderCountUser(
            counts = 0,
            totalAmountMonthly = BigDecimal.ZERO,
        )
    }

    override fun getUserOrdersCalculatedWithDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): OrderCalculatedResponse? {
        TODO("Not yet implemented")
    }
}
//Order service

//Payment service
interface PaymentService {
    fun getUserPayments(): List<PaymentResponse>
}

@Service
class PaymentServiceImpl(
    private val repository: PaymentRepository,
    private val securityUtils: SecurityUtils,
) : PaymentService {
    override fun getUserPayments(): List<PaymentResponse> {
        val responses: MutableList<PaymentResponse> = mutableListOf()
        val findPayments = repository.findByUserIdAndDeletedFalse(securityUtils.getCurrentUserId())

        findPayments?.forEach { payment ->
            responses.add(PaymentResponse(
                orderId = payment.order?.id,
                userId = payment.user.id,
                amount = payment.amount,
            ))
        }
        return responses
    }

}
//Payment service