package my.cashflow.account.infrastructure.service

import my.cashflow.account.domain.AccountAggregate
import my.cashflow.account.domain.AccountId
import my.cashflow.account.domain.DEFAULT_CURRENCY
import my.cashflow.account.domain.Money
import my.cashflow.account.infrastructure.AccountEventStore
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountService(
    private val eventStore: AccountEventStore
) : AccountServiceInterface {
    /**
     * Opens a new account with the given owner name and currency. If no currency is provided, it defaults to EUR.
     *
     * @param ownerName The name of the account owner.
     * @param currencyCode The currency code for the account (e.g., "USD", "EUR"). If null, defaults to EUR.
     * @return The ID of the newly opened account.
     *
     * @throws IllegalArgumentException if the owner name is blank or if the currency is invalid.
     * @throws RuntimeException if there is an error while saving the account events to the event store.
     */
    override fun openAccount(
        ownerName: String,
        currencyCode: String?
    ): AccountAggregate {
        val id = AccountId.new()

        val currency = currencyCode?.let { Currency.getInstance(it) } ?: DEFAULT_CURRENCY

        val (aggregate, event) = AccountAggregate.create(id, ownerName, currency)

        eventStore.appendEvents(id, listOf(event))
        return aggregate
    }

    /**
     * Deposits the specified amount of money into the account with the given ID.
     *
     * @param accountId The ID of the account to deposit into.
     * @param amount The amount of money to deposit.
     *
     * @throws IllegalArgumentException if the amount is zero or negative, or if the account ID is invalid.
     * @throws RuntimeException if there is an error while loading or saving events from/to the event store.
     */
    override fun deposit(accountId: AccountId, amount: Money): AccountAggregate {
        val (aggregate, event) = aggregateFor(accountId).deposit(amount)

        eventStore.appendEvents(accountId, listOf(event))
        return aggregate
    }

    /**
     * Withdraws the specified amount of money from the account with the given ID.
     *
     * @param accountId The ID of the account to withdraw from.
     * @param amount The amount of money to withdraw.
     *
     * @throws IllegalArgumentException if the amount is zero or negative, or if the account ID is invalid.
     * @throws RuntimeException if there is an error while loading or saving events from/to the event store, or if there are insufficient funds in the account.
     */
    override fun withdraw(accountId: AccountId, amount: Money): AccountAggregate {
        val (aggregate, event) = aggregateFor(accountId).withdraw(amount)
        eventStore.appendEvents(accountId, listOf(event))
        return aggregate
    }

    /**
     * Retrieves the current balance of the account with the given ID.
     *
     * @param accountId The ID of the account to check.
     * @return The current balance of the account.
     *
     * @throws IllegalArgumentException if the account ID is invalid.
     * @throws RuntimeException if there is an error while loading events from the event store.
     */
    override fun getBalance(accountId: AccountId): Money {
        val events = eventStore.loadEvents(accountId)
        val aggregate = AccountAggregate.fromEvents(events)
        return aggregate.balance
    }

    /**
     * Closes the account with the given ID.
     *
     * @param accountId The ID of the account to close.
     *
     * @throws IllegalArgumentException if the account ID is invalid.
     * @throws RuntimeException if there is an error while loading or saving events from/to the event store.
     */
    override fun closeAccount(accountId: AccountId) {
        val (_, event) = aggregateFor(accountId).close()
        eventStore.appendEvents(accountId, listOf(event))
    }

    private fun aggregateFor(accountId: AccountId): AccountAggregate {
        val events = eventStore.loadEvents(accountId)
        return AccountAggregate.fromEvents(events)
    }
}