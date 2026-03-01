package my.cashflow.account.infrastructure.repository.account

import my.cashflow.account.domain.account.AccountEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountEventRepository : JpaRepository<AccountEventEntity, UUID> {

    fun findByAccountIdOrderBySequenceNumberAsc(accountId: String): List<AccountEventEntity>

    fun countByAccountId(accountId: String): Long
}