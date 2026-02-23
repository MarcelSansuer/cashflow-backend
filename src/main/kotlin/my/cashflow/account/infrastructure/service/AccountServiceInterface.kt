package my.cashflow.account.infrastructure.service

import my.cashflow.account.domain.AccountAggregate
import my.cashflow.account.domain.AccountId
import my.cashflow.account.domain.Money
import java.util.Currency

interface AccountServiceInterface {

    fun openAccount(ownerName: String, currency: Currency): AccountAggregate

    fun getBalance(accountId: AccountId): Money

    fun deposit(accountId: AccountId, amount: Money): AccountAggregate

    fun withdraw(accountId: AccountId, amount: Money): AccountAggregate

    fun closeAccount(accountId: AccountId)
}