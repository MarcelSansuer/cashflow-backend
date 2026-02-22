package my.cashflow.account.api

import java.math.BigDecimal

data class OpenAccountRequest(
    val ownerName: String,
    val currency: String? = null,       // "EUR", "USD", ...
)

data class OpenAccountResponse(
    val accountId: String,
    val ownerName: String,
    val initialBalance: BigDecimal,
    val currency: String
)

data class DepositRequest(
    val accountId: String,
    val amount: BigDecimal,
    val currency: String? = null        // optional, sonst Default
)

data class DepositResponse(
    val accountId: String,
    val amount: BigDecimal,
    val newBalance: BigDecimal,
    val currency: String
)

data class WithdrawRequest(
    val accountId: String,
    val amount: BigDecimal,
    val currency: String? = null
)

data class WithdrawResponse(
    val accountId: String,
    val amount: BigDecimal,
    val newBalance: BigDecimal,
    val currency: String
)

data class BalanceRequest(
    val accountId: String
)

data class BalanceResponse(
    val accountId: String,
    val balance: BigDecimal,
    val currency: String
)
