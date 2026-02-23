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
     * Opens a new account with the specified owner name and currency.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun openAccount(@RequestBody request: OpenAccountRequest): OpenAccountResponse {
        val newAccount = accountService.openAccount(request.ownerName, request.currency)
        return accountApiMapper.toOpenAccountResponse(newAccount)
    }

    /**
     * Deposits the specified amount of money into the account with the given ID.
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

    @GetMapping("/{id}/balance")
    fun getBalance(@PathVariable id: String): BalanceResponse {
        val accountId = accountApiMapper.toAccountId(id)
        val money = accountService.getBalance(accountId)
        return accountApiMapper.toBalanceResponse(
            accountId = accountId,
            balance = money
        )
    }
}