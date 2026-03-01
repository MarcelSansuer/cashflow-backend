package my.cashflow.account.infrastructure.services.account

import my.cashflow.account.domain.account.AccountAggregate
import my.cashflow.account.domain.account.AccountId
import my.cashflow.account.domain.Money
import java.util.Currency

interface AccountServiceInterface {

    fun openAccount(ownerName: String, currency: Currency): AccountAggregate

    fun getBalance(accountId: AccountId): Money

    fun deposit(accountId: AccountId, amount: Money): AccountAggregate

    fun withdraw(accountId: AccountId, amount: Money): AccountAggregate

    fun closeAccount(accountId: AccountId)
}