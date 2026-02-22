package my.cashflow.account.infrastructure.repository

import my.cashflow.account.infrastructure.repository.AccountEventEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountEventRepository : JpaRepository<AccountEventEntity, UUID> {

    fun findByAccountIdOrderBySequenceNumberAsc(accountId: String): List<AccountEventEntity>

    fun countByAccountId(accountId: String): Long
}