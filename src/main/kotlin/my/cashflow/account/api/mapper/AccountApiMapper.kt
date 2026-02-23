package my.cashflow.account.api.mapper

import my.cashflow.account.api.BalanceResponse
import my.cashflow.account.api.DepositResponse
import my.cashflow.account.api.OpenAccountResponse
import my.cashflow.account.api.WithdrawResponse
import my.cashflow.account.domain.AccountAggregate
import my.cashflow.account.domain.AccountId
import my.cashflow.account.domain.DEFAULT_CURRENCY
import my.cashflow.account.domain.Money
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountApiMapper {

    fun toCurrency(code: String?): Currency =
        code?.let { Currency.getInstance(it) } ?: DEFAULT_CURRENCY

    fun toAccountId(id: String): AccountId =
        AccountId(id)

    fun toMoney(amount: Double, currencyCode: String?): Money {
        val currency = toCurrency(currencyCode)
        return Money.of(amount, currency)
    }

    fun toOpenAccountResponse(aggregate: AccountAggregate): OpenAccountResponse {
        return OpenAccountResponse(
            accountId = aggregate.id.value,
            ownerName = requireNotNull(aggregate.ownerName) { "Owner name must not be null for a valid account" },
            initialBalance = aggregate.balance.amount.toDouble(),
            currency = aggregate.balance.currency.currencyCode
        )
    }

    fun toWithdrawResponse(accountId: AccountId, amount: Money, newBalance: Money): WithdrawResponse {
        return WithdrawResponse(
            accountId = accountId.value,
            amount = amount.amount.toDouble(),
            newBalance = newBalance.amount.toDouble(),
            currency = newBalance.currency.currencyCode
        )
    }

    fun toDepositResponse(accountId: AccountId, amount: Money, newBalance: Money): DepositResponse {
        return DepositResponse(
            accountId = accountId.value,
            amount = amount.amount.toDouble(),
            newBalance = newBalance.amount.toDouble(),
            currency = newBalance.currency.currencyCode
        )
    }

    fun toBalanceResponse(accountId: AccountId, balance: Money): BalanceResponse {
        return BalanceResponse(
            accountId = accountId.value,
            currency = balance.currency.currencyCode,
            amount = balance.amount.toDouble(),
        )
    }
}