package my.cashflow.account.infrastructure.services.account

import my.cashflow.account.domain.account.AccountAggregate
import my.cashflow.account.domain.account.AccountId
import my.cashflow.account.domain.Money
import my.cashflow.account.infrastructure.repository.user.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountService(
    private val eventStore: AccountEventStore,
    private val userRepository: UserRepository
) : AccountServiceInterface {

    override fun openAccount(
        ownerUserId: UUID,
        creatorId: UUID,
        ownerName: String,
        currency: Currency
    ): AccountAggregate {
        require(userRepository.existsById(ownerUserId)) {
            "Owner user with ID $ownerUserId does not exist"
        }
        require(userRepository.existsById(creatorId)) {
            "Creator user with ID $creatorId does not exist"
        }

        val id = AccountId.new()
        val (aggregate, event) = AccountAggregate.create(id, ownerUserId, creatorId, ownerName, currency)

        eventStore.appendEvents(id, listOf(event))
        return aggregate
    }

    override fun deposit(accountId: AccountId, amount: Money): AccountAggregate {
        val (aggregate, event) = aggregateFor(accountId).deposit(amount)

        eventStore.appendEvents(accountId, listOf(event))
        return aggregate
    }

    override fun withdraw(accountId: AccountId, amount: Money): AccountAggregate {
        val (aggregate, event) = aggregateFor(accountId).withdraw(amount)
        eventStore.appendEvents(accountId, listOf(event))
        return aggregate
    }

    override fun getBalance(accountId: AccountId): AccountAggregate {
        val events = eventStore.loadEvents(accountId)
        val aggregate = AccountAggregate.fromEvents(events)
        return aggregate
    }

    override fun closeAccount(accountId: AccountId) {
        val (_, event) = aggregateFor(accountId).close()
        eventStore.appendEvents(accountId, listOf(event))
    }

    private fun aggregateFor(accountId: AccountId): AccountAggregate {
        val events = eventStore.loadEvents(accountId)
        return AccountAggregate.fromEvents(events)
    }
}
