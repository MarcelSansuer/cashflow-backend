package my.cashflow.account.api.account.mapper

import my.cashflow.account.api.account.BalanceResponse
import my.cashflow.account.api.account.DepositResponse
import my.cashflow.account.api.account.OpenAccountResponse
import my.cashflow.account.api.account.WithdrawResponse
import my.cashflow.account.domain.DEFAULT_CURRENCY
import my.cashflow.account.domain.Money
import my.cashflow.account.domain.account.AccountAggregate
import my.cashflow.account.domain.account.AccountId
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import java.math.BigDecimal
import java.util.*

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface AccountApiMapper {

    @Mapping(target = "accountId", source = "aggregate.id")
    @Mapping(target = "ownerUserId", source = "aggregate.ownerUserId")
    @Mapping(target = "creatorId", source = "aggregate.creatorId")
    @Mapping(target = "initialBalance", source = "aggregate.balance.amount")
    @Mapping(target = "currency", source = "aggregate.balance.currency.currencyCode")
    fun toOpenAccountResponse(aggregate: AccountAggregate): OpenAccountResponse

    @Mapping(target = "accountId", source = "aggregate.id")
    @Mapping(target = "ownerUserId", source = "aggregate.ownerUserId")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "newBalance", source = "aggregate.balance.amount")
    @Mapping(target = "currency", source = "aggregate.balance.currency.currencyCode")
    fun toWithdrawResponse(aggregate: AccountAggregate, amount: Money): WithdrawResponse

    @Mapping(target = "accountId", source = "aggregate.id")
    @Mapping(target = "ownerUserId", source = "aggregate.ownerUserId")
    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "newBalance", source = "aggregate.balance.amount")
    @Mapping(target = "currency", source = "aggregate.balance.currency.currencyCode")
    fun toDepositResponse(aggregate: AccountAggregate, amount: Money): DepositResponse

    @Mapping(target = "accountId", source = "aggregate.id")
    @Mapping(target = "ownerUserId", source = "aggregate.ownerUserId")
    @Mapping(target = "amount", source = "aggregate.balance.amount")
    @Mapping(target = "currency", source = "aggregate.balance.currency.currencyCode")
    fun toBalanceResponse(aggregate: AccountAggregate): BalanceResponse

    fun toCurrency(code: String?): Currency =
        code?.let { Currency.getInstance(it) } ?: DEFAULT_CURRENCY

    fun toAccountId(id: String): AccountId = AccountId(id)

    fun map(id: AccountId): String = id.value

    fun map(value: BigDecimal): Double = value.toDouble()

    fun toMoney(amount: Double, currencyCode: String?): Money {
        val currency = toCurrency(currencyCode)
        return Money.of(amount, currency)
    }
}
