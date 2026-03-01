package my.cashflow.account.domain.account

import my.cashflow.account.domain.DEFAULT_CURRENCY
import my.cashflow.account.domain.Money
import java.time.Instant
import java.util.Currency
import java.util.UUID

class AccountAggregate private constructor(
    val id: AccountId,
    val ownerUserId: UUID?,
    val creatorId: UUID?,
    val ownerName: String?,
    val balance: Money,
    val closed: Boolean,
) {
    companion object {

        /**
         * Creates a new account aggregate with the given ID, owner name, and optional currency (defaulting to EUR).
         * Initializes the account with a zero balance and generates an AccountOpened event.
         *
         * @param id The unique identifier for the account.
         * @param ownerUserId The unique identifier for the user who owns the account.
         * @param creatorId The unique identifier for the user who created the account.
         * @param ownerName The name of the account owner.
         * @param currency The currency of the account (optional, defaults to EUR).
         * @return A pair containing the newly created AccountAggregate and the AccountOpened event.
         * @throws IllegalArgumentException if the owner name is blank or if the currency is invalid.
         */
        fun create(
            id: AccountId,
            ownerUserId: UUID,
            creatorId: UUID,
            ownerName: String,
            currency: Currency = DEFAULT_CURRENCY
        ): Pair<AccountAggregate, AccountOpened> {
            val initialBalance = Money.Companion.zero(currency)
            val event = AccountOpened(
                accountId = id,
                ownerUserId = ownerUserId,
                creatorId = creatorId,
                ownerName = ownerName,
                initialBalance = initialBalance,
                occurredAt = Instant.now()
            )
            val aggregate = AccountAggregate(id, null, null, null, initialBalance, closed = false).applyEvent(event)
            return aggregate to event
        }

        fun fromEvents(events: List<AccountEvent>): AccountAggregate {
            require(events.isNotEmpty()) { "No events found for account" }
            
            // Der erste Event MUSS ein AccountOpened sein
            val firstEvent = events.first()
            require(firstEvent is AccountOpened) { "First event must be AccountOpened" }

            var state = AccountAggregate(
                id = firstEvent.accountId,
                ownerUserId = null,
                creatorId = null,
                ownerName = null,
                balance = Money.Companion.zero(),
                closed = false
            )
            
            events.forEach { state = state.applyEvent(it) }
            return state
        }
    }

    fun deposit(amount: Money): Pair<AccountAggregate, MoneyDeposited> {
        requireNotClosed()
        requireGreaterThanZero(amount)

        val event = MoneyDeposited(
            accountId = id,
            occurredAt = Instant.now(),
            amount = amount
        )

        val newState = applyEvent(event)
        return newState to event
    }

    fun withdraw(amount: Money): Pair<AccountAggregate, MoneyWithdrawn> {
        requireNotClosed()
        requireGreaterThanZero(amount)

        Money.Companion.checkInsufficientFunds(balance, amount)

        val event = MoneyWithdrawn(
            accountId = id,
            occurredAt = Instant.now(),
            amount = amount
        )

        val newState = applyEvent(event)
        return newState to event
    }

    fun close(): Pair<AccountAggregate, AccountClosed> {
        requireNotClosed()

        val event = AccountClosed(
            accountId = id,
            occurredAt = Instant.now()
        )

        val newState = applyEvent(event)
        return newState to event
    }

    private fun requireNotClosed() {
        require(!closed) { "Account is closed" }
    }

    private fun requireGreaterThanZero(amount: Money) {
        require(!amount.isZero()) { "Amount must be greater than zero" }
    }

    private fun copy(
        id: AccountId = this.id,
        ownerUserId: UUID? = this.ownerUserId,
        creatorId: UUID? = this.creatorId,
        ownerName: String? = this.ownerName,
        balance: Money = this.balance,
        closed: Boolean = this.closed
    ) = AccountAggregate(id, ownerUserId, creatorId, ownerName, balance, closed)

    private fun applyEvent(event: AccountEvent): AccountAggregate =
        when (event) {
            is AccountOpened -> AccountAggregate(
                id = event.accountId,
                ownerUserId = event.ownerUserId,
                creatorId = event.creatorId,
                ownerName = event.ownerName,
                balance = event.initialBalance,
                closed = false
            )

            is MoneyDeposited -> copy(
                balance = balance + event.amount
            )

            is MoneyWithdrawn -> copy(
                balance = balance - event.amount
            )

            is AccountClosed -> copy(
                closed = true
            )
        }
}
