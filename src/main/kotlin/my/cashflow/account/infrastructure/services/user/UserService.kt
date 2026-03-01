package my.cashflow.account.infrastructure.services.user

import my.cashflow.account.api.user.CreateUserRequest
import my.cashflow.account.api.user.CreateUserResponse
import my.cashflow.account.api.user.GetUserResponse
import my.cashflow.account.api.user.mapper.UserApiMapper
import my.cashflow.account.infrastructure.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userApiMapper: UserApiMapper,
) {


    @Transactional
    fun createUser(request: CreateUserRequest): CreateUserResponse {
        request.validate()

        require(!userRepository.existsByUserName(request.userName)) {
            "User with username ${request.userName} already exists"
        }

        val userEntity = userApiMapper.toUserEntity(request)
        val savedUser = userRepository.save(userEntity)

        return userApiMapper.toCreateUserResponse(savedUser)
    }

    fun getUser(userId: String): GetUserResponse {
        val user = userRepository.findUserEntityById(
            userId = UUID.fromString(userId)
        ) ?: throw IllegalArgumentException("User with id $userId not found")

        return userApiMapper.toGetUserResponse(user)
    }

}
