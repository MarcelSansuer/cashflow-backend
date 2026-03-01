package my.cashflow.account.domain.account

import java.util.*

data class AccountId(val value: String) {
    companion object {
        fun new(): AccountId = AccountId(UUID.randomUUID().toString())
    }
}
