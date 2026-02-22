package my.cashflow.account.api.mapper

import my.cashflow.account.api.OpenAccountResponse
import my.cashflow.account.api.WithdrawResponse
import my.cashflow.account.domain.AccountId
import my.cashflow.account.domain.DEFAULT_CURRENCY
import my.cashflow.account.domain.Money
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Currency

@Component
class AccountApiMapper {

    fun toCurrency(code: String?): Currency =
        code?.let { Currency.getInstance(it) } ?: DEFAULT_CURRENCY

    fun toAccountId(id: String): AccountId =
        AccountId(id)

    fun toMoney(amount: BigDecimal, currencyCode: String?): Money {
        val currency = toCurrency(currencyCode)
        return Money(
            amount = amount,
            currency = currency
        )
    }

    fun toOpenAccountResponse(accountId: AccountId, ownerName: String, balance: Money): OpenAccountResponse {
        return OpenAccountResponse(
            accountId = accountId.value,
            ownerName = ownerName,
            initialBalance = balance.amount,
            currency = balance.currency.currencyCode
        )

        fun toWithdrawResponse(accountId: AccountId, amount: Money, newBalance: Money): WithdrawResponse {
            return WithdrawResponse(
                accountId = accountId.value,
                amount = amount.amount,
                newBalance = newBalance.amount,
                currency = newBalance.currency.currencyCode
            )
        }

        fun toDepositResponse(accountId: AccountId, amount: Money, newBalance: Money): WithdrawResponse {
            return WithdrawResponse(
                accountId = accountId.value,
                amount = amount.amount,
                newBalance = newBalance.amount,
                currency = newBalance.currency.currencyCode
            )
        }

        fun toBalanceResponse(accountId: AccountId, balance: Money): WithdrawResponse {
            return WithdrawResponse(
                accountId = accountId.value,
                amount = balance.amount,
                newBalance = balance.amount,
                currency = balance.currency.currencyCode
            )
        }
    }
}