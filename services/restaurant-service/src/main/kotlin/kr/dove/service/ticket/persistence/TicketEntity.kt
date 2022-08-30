package kr.dove.service.ticket.persistence

import core.state.State
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(value = "TICKETS")
data class TicketEntity(
    @Id val id: String,
    var state: State = State.PENDING,
    val orderId: String,
    val restaurantId: String,
    var acceptTime: LocalDateTime? = null,
    var readyTime: LocalDateTime? = null,
    var preparingTime: Long? = null,
    var pickedUpTime: LocalDateTime? = null,
    val orderItemsAsJson: String,
    @Version val version: Int = 0,
)