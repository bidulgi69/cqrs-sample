package core.event

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Event<K, T>(
    @field:JsonProperty("type") val type: EventType,
    @field:JsonProperty("key") val key: K,
    @field:JsonProperty("data") val data: T,
    @field:JsonProperty("publishedAt") val publishedAt: LocalDateTime = LocalDateTime.now()
)

enum class EventType {
    ORDER_CREATED,
    ORDER_APPROVED,
    ORDER_REJECTED,

    CUSTOMER_APPROVED,
    CUSTOMER_REJECTED,

    PAYMENT_APPROVED,
    PAYMENT_REJECTED,

    TICKET_CREATED,
    TICKET_REJECTED,
    TICKET_APPROVED,

    KITCHEN_PREPARING,
    KITCHEN_READY,

    DELIVERY_PICKEDUP,
    DELIVERY_COMPLETED,
}