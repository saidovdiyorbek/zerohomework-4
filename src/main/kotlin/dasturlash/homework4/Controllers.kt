package dasturlash.homework4

import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest, request: HttpServletRequest) =
        authService.login(loginRequest, request)
}

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    service: UserService
) {


    @Operation(summary = "Create user")
    @PostMapping
        fun create(@RequestBody userRequest: UserRequest) = userService.create(userRequest)

    @Operation(summary = "Get one user by id Admin")
    @GetMapping("/{id}")
    fun getOneAdmin(@PathVariable id: Long): UserFullInfoAdmin = userService.getOneAdmin(id)

    @Operation(summary = "Delete user bu id")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @Operation(summary = "Get all users")
    @GetMapping
    fun getAll(): List<UserFullInfoAdmin> = userService.getAllAdmin()

    @Operation(summary = "Update user by id")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updateBody: UpdateUserRequest) =
        userService.update(id, updateBody)

}

//category controller
@RestController
@RequestMapping("/api/category")
class CategoryController(
    private val categoryService: CategoryService
){

    @Operation(summary = "Create category")
    @PostMapping
    fun create(@RequestBody request: CategoryCreateRequest) = categoryService.create(request)

    @Operation(summary = "Get all categories")
    @GetMapping
    fun getAll(): List<CategoryFullInfo> = categoryService.getAll()

    @Operation(summary = "Get one by id")
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): CategoryFullInfo = categoryService.getOne(id)

    @Operation(summary = "Update by id and body")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updateBody: CategoryUpdateRequest) = categoryService.update(id, updateBody)

    @Operation(summary = "Delete by id")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)
}

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
){

    @Operation(summary = "Create product")
    @PostMapping
    fun create(@RequestBody request: ProductCreateRequest) = productService.create(request)

    @Operation(summary = "Get all products")
    @GetMapping
    fun getAll(): List<ProductFullInfo> = productService.getAll()

    @Operation(summary = "Get one by id")
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): ProductFullInfo = productService.getOne(id)

    @Operation(summary = "Update by id and body")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updateBody: ProductUpdateRequest) = productService.update(id, updateBody)

    @Operation(summary = "Delete by id")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)
}

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
){
    @Operation(summary = "Create order")
    @PostMapping
    fun create(@RequestBody orderRequest: OrderCreateRequest) = orderService.create(orderRequest)
}