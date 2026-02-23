package my.cashflow.account.api

data class OpenAccountRequest(
    val ownerName: String,
    val currency: String,
)

data class OpenAccountResponse(
    val accountId: String,
    val ownerName: String,
    val initialBalance: Double,
    val currency: String
)

data class DepositRequest(
    val amount: Double,
    val currency: String? = null        // optional, sonst Default
)

data class DepositResponse(
    val accountId: String,
    val amount: Double,
    val newBalance: Double,
    val currency: String
)

data class WithdrawRequest(
    val amount: Double,
    val currency: String? = null
)

data class WithdrawResponse(
    val accountId: String,
    val amount: Double,
    val newBalance: Double,
    val currency: String
)

data class BalanceRequest(
    val accountId: String
)

data class BalanceResponse(
    val accountId: String,
    val amount: Double,
    val currency: String
)
