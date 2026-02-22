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
){
    /**
     * Opens a new account with the given owner name and currency. If no currency is provided, it defaults to EUR.
     *
     * @param ownerName The name of the account owner.
     * @param currency The currency of the account (optional, defaults to EUR).
     * @return The ID of the newly opened account.
     *
     * @throws IllegalArgumentException if the owner name is blank or if the currency is invalid.
     * @throws RuntimeException if there is an error while saving the account events to the event store.
     */
    fun openAccount(
        ownerName: String,
        currency: Currency = DEFAULT_CURRENCY
    ): AccountId {
        val id = AccountId.new()
        val (aggregate, event) = AccountAggregate.create(id, ownerName, currency)

        eventStore.appendEvents(id, listOf(event))
        return aggregate.id
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
    fun deposit(accountId: AccountId, amount: Money) {
        val (_, event) = aggregateFor(accountId).deposit(amount)

        eventStore.appendEvents(accountId, listOf(event))
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
    fun withdraw(accountId: AccountId, amount: Money) {
        val (_, event) = aggregateFor(accountId).withdraw(amount)
        eventStore.appendEvents(accountId, listOf(event))
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
    fun getBalance(accountId: AccountId): Money {
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
    fun closeAccount(accountId: AccountId) {
        val (_, event) = aggregateFor(accountId).close()
        eventStore.appendEvents(accountId, listOf(event))
    }

    private fun aggregateFor(accountId: AccountId): AccountAggregate {
        val events = eventStore.loadEvents(accountId)
        return AccountAggregate.fromEvents(events)
    }
}