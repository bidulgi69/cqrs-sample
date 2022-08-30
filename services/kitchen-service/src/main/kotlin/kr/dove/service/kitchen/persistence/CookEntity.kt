package kr.dove.service.kitchen.persistence

import core.state.State
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(value = "COOKS")
data class CookEntity(
    @Id val id: String,
    var state: State = State.PREPARING,
    val orderId: String,
    val ticketId: String,
    val acceptTime: LocalDateTime,
    var readyTime: LocalDateTime? = null,
    @Version val version: Int = 0,
)