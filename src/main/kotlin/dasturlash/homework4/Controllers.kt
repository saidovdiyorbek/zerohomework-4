package dasturlash.homework4

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    service: UserService
) {


    @Operation(summary = "Create user")
    @PostMapping
        fun create(@RequestBody userRequest: UserRequest) = userService.create(userRequest)

    @Operation(summary = "Get one user by id")
    @GetMapping("/{id}")
    fun getOne(@PathVariable id: Long): UserFullInfo = userService.getOne(id)

    @Operation(summary = "Delete user bu id")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @Operation(summary = "Get all users")
    @GetMapping
    fun getAll(): List<UserFullInfo> = userService.getAll()

    @Operation(summary = "Update user by id")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody updateBody: UpdateUserRequest) =
        userService.update(id, updateBody)

    @Operation(summary = "Get user all payments")
    @PostMapping("/{id}")
    fun getUserAllPayments(@PathVariable id: Long): List<UserPaymentTransactionResponse> =
        userService.getAllPayments(id)
}