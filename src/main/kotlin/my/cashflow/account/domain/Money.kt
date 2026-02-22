package my.cashflow.account.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency

data class Money(
    val amount: BigDecimal,
    val currency: Currency
) : Comparable<Money> {

    init {
        require(amount.scale() <= 2) { "Money supports max 2 decimal places." }
    }

    operator fun plus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amount = this.amount + other.amount)
    }

    operator fun minus(other: Money): Money {
        requireSameCurrency(other)
        return copy(amount = this.amount - other.amount)
    }

    operator fun times(multiplier: BigDecimal): Money =
        copy(amount = amount.multiply(multiplier).setScale(2, RoundingMode.HALF_EVEN))

    operator fun div(divisor: BigDecimal): Money =
        copy(amount = amount.divide(divisor, 2, RoundingMode.HALF_EVEN))

    override fun compareTo(other: Money): Int {
        requireSameCurrency(other)
        return this.amount.compareTo(other.amount)
    }

    fun isZero(): Boolean =
        amount.compareTo(BigDecimal.ZERO) == 0

    private fun requireSameCurrency(other: Money) {
        require(currency == other.currency) {
            "Cannot operate on different currencies: $currency and ${other.currency}"
        }
    }

    override fun toString(): String =
        "${amount.setScale(2, RoundingMode.HALF_EVEN)} ${currency.currencyCode}"

    companion object {
        fun of(amount: Double, currency: Currency = DEFAULT_CURRENCY): Money =
            Money(
                BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_EVEN),
                currency
            )

        fun zero(currency: Currency = DEFAULT_CURRENCY): Money =
            of(0.0, currency)

        fun checkInsufficientFunds(balance: Money, withdrawalAmount: Money) {
            require(balance >= withdrawalAmount) {
                "Insufficient funds: Balance is $balance, but withdrawal amount is $withdrawalAmount"
            }
        }
    }
}
