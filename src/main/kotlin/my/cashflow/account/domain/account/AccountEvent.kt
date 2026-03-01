package my.cashflow.account.domain.account

import my.cashflow.account.domain.Money
import java.time.Instant

sealed class AccountEvent {
    abstract val accountId: AccountId
    abstract val occurredAt: Instant
}

data class AccountOpened(
    override val accountId: AccountId,
    override val occurredAt: Instant,
    val ownerName: String,
    val initialBalance: Money
) : AccountEvent()

data class MoneyDeposited(
    override val accountId: AccountId,
    override val occurredAt: Instant,
    val amount: Money
) : AccountEvent()

data class MoneyWithdrawn(
    override val accountId: AccountId,
    override val occurredAt: Instant,
    val amount: Money
) : AccountEvent()

data class AccountClosed(
    override val accountId: AccountId,
    override val occurredAt: Instant,
) : AccountEvent()
