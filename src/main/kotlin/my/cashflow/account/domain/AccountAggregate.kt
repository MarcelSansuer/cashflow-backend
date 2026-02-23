package my.cashflow.account.domain

import java.time.Instant
import java.util.Currency

class AccountAggregate private constructor(
    val id: AccountId,
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
         * @param ownerName The name of the account owner.
         * @param currency The currency of the account (optional, defaults to EUR).
         * @return A pair containing the newly created AccountAggregate and the AccountOpened event.
         * @throws IllegalArgumentException if the owner name is blank or if the currency is invalid.
         */
        fun create(id: AccountId, ownerName: String, currency: Currency = DEFAULT_CURRENCY): Pair<AccountAggregate, AccountOpened> {
            val initialBalance = Money.zero(currency)
            val event = AccountOpened(
                accountId = id,
                ownerName = ownerName,
                initialBalance = initialBalance,
                occurredAt = Instant.now()
            )
            val aggregate = AccountAggregate(id, null, initialBalance, closed = false).applyEvent(event)
            return aggregate to event
        }

        fun fromEvents(events: List<AccountEvent>): AccountAggregate {
            // Dummy-Startzustand; wird vom ersten AccountOpened überschrieben
            var state = AccountAggregate(AccountId("DUMMY"), null, Money.zero(), closed = false)
            events.forEach { state = state.applyEvent(it) }
            return state
        }
    }

    /**
     * Add a deposit to the account. Validates that the amount is greater than zero and returns a new AccountAggregate
     * with the updated balance, along with the MoneyDeposited event.
     *
     * @param amount The amount of money to deposit. Must be greater than zero.
     * @return A pair containing the new AccountAggregate with the updated balance and the MoneyDepos
     */
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

    /**
     * Withdraws a specified amount of money from the account. Validates that the amount is greater than zero and that there are sufficient funds in the account.
     * Returns a new AccountAggregate with the updated balance, along with the MoneyWithdrawn event.
     *
     * @param amount The amount of money to withdraw. Must be greater than zero and less than or equal to the current balance.
     * @return A pair containing the new AccountAggregate with the updated balance and the MoneyWithdrawn event.
     * @throws IllegalArgumentException if the withdrawal amount is zero or negative, or if there are insufficient funds in the account.
     */
    fun withdraw(amount: Money): Pair<AccountAggregate, MoneyWithdrawn> {
        requireNotClosed()
        requireGreaterThanZero(amount)

        // wirft selbst eine Exception, wenn nicht genug Guthaben
        Money.checkInsufficientFunds(balance, amount)

        val event = MoneyWithdrawn(
            accountId = id,
            occurredAt = Instant.now(),
            amount = amount
        )

        val newState = applyEvent(event)
        return newState to event
    }

    /**
     * Closes the account. Returns a new AccountAggregate with the same state (since we don't need to change any fields to represent a closed account)
     * and an AccountClosed event.
     *
     * @return A pair containing the new AccountAggregate (which is the same as the current state) and the AccountClosed event.
     */
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

    /**
     * Creates a copy of the current AccountAggregate with the specified fields updated. This is a helper function to create new instances of AccountAggregate with modified state after applying events.
     *
     * @param id The ID of the account (defaults to the current ID).
     * @param ownerName The name of the account owner (defaults to the current owner name
     * @param balance The current balance of the account (defaults to the current balance).
     * @param closed Whether the account is closed (defaults to the current closed status).
     * @return A new AccountAggregate with the specified fields updated.
     */
    private fun copy(
        id: AccountId = this.id,
        ownerName: String? = this.ownerName,
        balance: Money = this.balance,
        closed: Boolean = this.closed
    ) = AccountAggregate(id, ownerName, balance, closed)

    /**
     * Applies the given AccountEvent to the current state of the AccountAggregate and returns a new AccountAggregate with the updated state.
     *
     * @param event The AccountEvent to apply. Must be one of AccountOpened, MoneyDeposited, MoneyWithdrawn, or AccountClosed.
     * @return A new AccountAggregate with the updated state after applying the event.
     * @throws IllegalArgumentException if the event type is unknown or if there is an error while applying the event.
     */
    private fun applyEvent(event: AccountEvent): AccountAggregate =
        when (event) {
            is AccountOpened -> AccountAggregate(
                id = event.accountId,
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
