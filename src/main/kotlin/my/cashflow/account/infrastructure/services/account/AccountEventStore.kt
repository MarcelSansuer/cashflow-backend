package my.cashflow.account.infrastructure.services.account

import my.cashflow.account.domain.account.AccountClosed
import my.cashflow.account.domain.account.AccountEvent
import my.cashflow.account.domain.account.AccountEventEntity
import my.cashflow.account.domain.account.AccountId
import my.cashflow.account.domain.account.AccountOpened
import my.cashflow.account.domain.account.MoneyDeposited
import my.cashflow.account.domain.account.MoneyWithdrawn
import my.cashflow.account.infrastructure.repository.account.AccountEventRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@Component
class AccountEventStore(
    private val repository: AccountEventRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional(readOnly = true)
    fun loadEvents(accountId: AccountId): List<AccountEvent> {
        val entities = repository.findByAccountIdOrderBySequenceNumberAsc(accountId.value)
        return entities.map { deserializeEvent(it.eventType, it.payload) }
    }

    @Transactional
    fun appendEvents(accountId: AccountId, newEvents: List<AccountEvent>) {
        var sequence = repository.countByAccountId(accountId.value)

        val entities = newEvents.map { event ->
            sequence += 1
            val (eventType, json) = serializeEvent(event)
            AccountEventEntity(
                accountId = accountId.value,
                sequenceNumber = sequence,
                eventType = eventType,
                payload = json,
                occurredAt = event.occurredAt,
                eventFlag = eventFlagFor(event)
            )
        }
        repository.saveAll(entities)
    }

    private fun serializeEvent(event: AccountEvent): Pair<String, String> {
        val type = event::class.simpleName
            ?: throw IllegalArgumentException("Cannot determine event type for $event")
        val json = objectMapper.writeValueAsString(event)
        return type to json
    }

    private fun deserializeEvent(type: String, json: String): AccountEvent =
        when (type) {
            AccountOpened::class.simpleName ->
                objectMapper.readValue(json, AccountOpened::class.java)
            MoneyDeposited::class.simpleName ->
                objectMapper.readValue(json, MoneyDeposited::class.java)
            MoneyWithdrawn::class.simpleName ->
                objectMapper.readValue(json, MoneyWithdrawn::class.java)
            AccountClosed::class.simpleName ->
                objectMapper.readValue(json, AccountClosed::class.java)
            else -> throw IllegalArgumentException("Unknown event type: $type")
        }

    private fun eventFlagFor(event: AccountEvent): String =
        when (event) {
            is AccountOpened -> "OPENED"
            is MoneyDeposited -> "DEPOSITED"
            is MoneyWithdrawn -> "WITHDRAWN"
            is AccountClosed -> "CLOSED"
        }
}