package my.cashflow.account.api.user.mapper

import my.cashflow.account.api.user.CreateUserRequest
import my.cashflow.account.api.user.CreateUserResponse
import my.cashflow.account.api.user.GetUserResponse
import my.cashflow.account.domain.user.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.Named
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface UserApiMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", qualifiedByName = ["toIso"])
    @Mapping(target = "closedAt", qualifiedByName = ["toIsoNullable"])
    @Mapping(target = "changedAt", qualifiedByName = ["toIsoNullable"])
    fun toGetUserResponse(user: UserEntity): GetUserResponse

    fun toCreateUserEntity(request: CreateUserRequest): UserEntity

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", qualifiedByName = ["toIso"])
    fun toCreateUserResponse(user: UserEntity): CreateUserResponse

    @Mapping(target = "passwordHash", source = "password")
    fun toUserEntity(request: CreateUserRequest): UserEntity

    fun map(uuid: UUID?): String? = uuid?.toString()

    fun map(instant: Instant?): String? = instant?.toString()

    fun map(enumValue: Enum<*>?): String? = enumValue?.name

    companion object {
        private val ISO: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

        @JvmStatic
        @Named("toIso")
        fun toIso(instant: Instant): String =
            ISO.format(instant)

        @JvmStatic
        @Named("toIsoNullable")
        fun toIsoNullable(instant: Instant?): String? =
            instant?.let(ISO::format)
    }
}
