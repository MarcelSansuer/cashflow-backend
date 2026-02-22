package my.cashflow.account.api

import my.cashflow.account.infrastructure.service.AccountService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/accounts")
class AccountController(
    private val accountService: AccountService
) {

}