package my.cashflow.account.api.user

import my.cashflow.account.api.user.mapper.UserApiMapper
import my.cashflow.account.infrastructure.services.user.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/users"])
class UserController(
    private val userService: UserService,
    private val mapper: UserApiMapper
) {

    @PostMapping
    fun createUser(request: CreateUserRequest): CreateUserResponse {
        return userService.createUser(request)
    }


    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): GetUserResponse{
        return userService.getUser(id)
    }

}