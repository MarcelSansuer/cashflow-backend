package my.cashflow.account.infrastructure.repository.user

import my.cashflow.account.domain.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<UserEntity, UUID> {

    fun existsByUserName(username: String): Boolean

    fun findUserEntityById(userId: UUID): UserEntity?
}