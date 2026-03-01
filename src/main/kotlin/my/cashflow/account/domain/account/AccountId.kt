package my.cashflow.account.domain.account

import java.util.*

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

data class AccountId @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
    @get:JsonValue val value: String
) {
    companion object {
        fun new(): AccountId = AccountId(UUID.randomUUID().toString())
    }
}
