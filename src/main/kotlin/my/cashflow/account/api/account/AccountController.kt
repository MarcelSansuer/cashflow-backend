package my.cashflow.account.api.account

import my.cashflow.account.api.account.mapper.AccountApiMapper
import my.cashflow.account.domain.account.AccountId
import my.cashflow.account.infrastructure.services.account.AccountServiceInterface
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountServiceInterface,
    private val accountApiMapper: AccountApiMapper
) {

    @GetMapping("/{id}/balance")
    fun getBalance(@PathVariable id: String): BalanceResponse {
        val accountId = accountApiMapper.toAccountId(id)
        val aggregate = accountService.getBalance(accountId)
        return accountApiMapper.toBalanceResponse(aggregate)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun openAccount(@RequestBody request: OpenAccountRequest): OpenAccountResponse {
        val currency = accountApiMapper.toCurrency(request.currency)

        val newAccount = accountService.openAccount(
            ownerUserId = request.ownerUserId,
            creatorId = request.creatorId,
            ownerName = request.ownerName,
            currency = currency
        )

        return accountApiMapper.toOpenAccountResponse(newAccount)
    }

    @PostMapping("/{id}/deposit")
    fun deposit(
        @PathVariable id: String,
        @RequestBody request: DepositRequest
    ): DepositResponse {
        val accountId = accountApiMapper.toAccountId(id)
        val money = accountApiMapper.toMoney(request.amount, request.currency)
        val updatedAccount = accountService.deposit(accountId, money)

        return accountApiMapper.toDepositResponse(updatedAccount, money)
    }

    @PostMapping("/{id}/withdraw")
    fun withdraw(
        @PathVariable id: String,
        @RequestBody request: WithdrawRequest
    ): WithdrawResponse {
        val accountId = AccountId(id)
        val money = accountApiMapper.toMoney(request.amount, request.currency)
        val updatedAccount = accountService.withdraw(accountId, money)

        return accountApiMapper.toWithdrawResponse(updatedAccount, money)
    }

    @PostMapping("/{id}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun closeAccount(@PathVariable id: String) {
        val accountId = accountApiMapper.toAccountId(id)
        accountService.closeAccount(accountId)
    }
}
