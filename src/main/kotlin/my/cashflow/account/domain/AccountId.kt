package my.cashflow.account.domain

import java.util.*

@JvmInline
value class AccountId(val value: String) {
    companion object {
        fun new(): AccountId = AccountId(UUID.randomUUID().toString())
    }
}
