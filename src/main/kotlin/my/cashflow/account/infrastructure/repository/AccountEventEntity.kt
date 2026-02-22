package my.cashflow.account.infrastructure.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "account_events")
class AccountEventEntity(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "account_id", nullable = false)
    val accountId: String,

    @Column(name = "sequence_number", nullable = false)
    val sequenceNumber: Long,

    @Column(name = "event_type", nullable = false)
    val eventType: String,

    @Lob
    @Column(name = "payload", nullable = false)
    val payload: String,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: Instant,

    @Column(name = "event_flag", nullable = false)
    val eventFlag: String
)