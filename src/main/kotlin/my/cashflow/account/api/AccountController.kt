package my.cashflow.account.api

import my.cashflow.account.api.mapper.AccountApiMapper
import my.cashflow.account.domain.AccountId
import my.cashflow.account.infrastructure.service.AccountServiceInterface
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountServiceInterface,
    private val accountApiMapper: AccountApiMapper
) {

    /**
     * Retrieves the current balance of the account with the specified ID.
     *
     * @param id The ID of the account to retrieve the balance for.
     * @return A [BalanceResponse] containing the account ID, currency, and current balance amount.
     *
     * @throws IllegalArgumentException if the account ID is invalid or if there is an error while retrieving the balance.
     */
    @GetMapping("/{id}/balance")
    fun getBalance(@PathVariable id: String): BalanceResponse {
        val accountId = accountApiMapper.toAccountId(id)
        val money = accountService.getBalance(accountId)
        return accountApiMapper.toBalanceResponse(
            accountId = accountId,
            balance = money
        )
    }

    /**
     * Opens a new account with the specified owner name and currency.
     *
     * @param request An [OpenAccountRequest] containing the owner name and optional currency code for the new account.
     * @return An [OpenAccountResponse] containing the ID, owner name, initial balance
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun openAccount(@RequestBody request: OpenAccountRequest): OpenAccountResponse {
        val currency = accountApiMapper.toCurrency(request.currency)

        val newAccount = accountService.openAccount(request.ownerName, currency)

        return accountApiMapper.toOpenAccountResponse(newAccount)
    }

    /**
     * Deposits the specified amount of money into the account with the given ID.
     *
     * @param id The ID of the account to deposit into.
     * @param request A [DepositRequest] containing the amount and currency to deposit.
     * @return A [DepositResponse] containing the account ID, deposited amount, new balance
     * @throws IllegalArgumentException if the account ID is invalid, if the amount is zero or negative, or if there is an error while processing the deposit.
     */
    @PostMapping("/{id}/deposit")
    fun deposit(
        @PathVariable id: String,
        @RequestBody request: DepositRequest
    ): DepositResponse {
        val accountId = accountApiMapper.toAccountId(id)
        val money = accountApiMapper.toMoney(request.amount, request.currency)
        val updatedAccount = accountService.deposit(accountId, money)

        return accountApiMapper.toDepositResponse(
            accountId = accountId,
            amount = money,
            newBalance = updatedAccount.balance
        )
    }

    /**
     * Withdraws the specified amount of money from the account with the given ID.
     *
     * @param id The ID of the account to withdraw from.
     * @param request A [WithdrawRequest] containing the amount and currency to withdraw.
     * @return A [WithdrawResponse] containing the account ID, withdrawn amount, new balance
     * @throws IllegalArgumentException if the account ID is invalid, if the amount is zero or negative, if there are insufficient funds in the account, or if there is an error while processing the withdrawal.
     */
    @PostMapping("/{id}/withdraw")
    fun withdraw(
        @PathVariable id: String,
        @RequestBody request: WithdrawRequest
    ): WithdrawResponse {
        val accountId = AccountId(id)
        val money = accountApiMapper.toMoney(request.amount, request.currency)
        val updatedAccount = accountService.withdraw(accountId, money)

        return accountApiMapper.toWithdrawResponse(
            accountId = accountId,
            amount = money,
            newBalance = updatedAccount.balance
        )
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun closeAccount(@PathVariable id: String) {
        val accountId = accountApiMapper.toAccountId(id)
        accountService.closeAccount(accountId)
    }
}