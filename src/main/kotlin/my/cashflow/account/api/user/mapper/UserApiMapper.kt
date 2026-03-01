package my.cashflow.account.api.user.mapper

import my.cashflow.account.api.user.CreateUserRequest
import my.cashflow.account.api.user.CreateUserResponse
import my.cashflow.account.api.user.GetUserResponse
import my.cashflow.account.domain.user.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import java.time.Instant
import java.util.*

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
abstract class UserApiMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    abstract fun toCreateUserResponse(user: UserEntity): CreateUserResponse

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "closedAt", source = "closedAt")
    @Mapping(target = "changedAt", source = "changedAt")
    abstract fun toGetUserResponse(user: UserEntity): GetUserResponse

    fun toUserEntity(request: CreateUserRequest): UserEntity {
        return UserEntity(
            userName = request.userName,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            age = request.age ?: 0,
            passwordHash = request.password // In reality, you'd hash it here or in service
        )
    }

    fun map(uuid: UUID?): String? = uuid?.toString()

    fun map(instant: Instant?): String? = instant?.toString()

    fun map(enumValue: Enum<*>?): String? = enumValue?.name
}
