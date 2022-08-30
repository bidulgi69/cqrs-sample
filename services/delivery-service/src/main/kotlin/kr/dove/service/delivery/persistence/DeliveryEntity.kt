package kr.dove.service.delivery.persistence

import core.state.State
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(value = "DELIVERIES")
data class DeliveryEntity(
    @Id val id: String,
    var state: State = State.PICKEDUP,
    val orderId: String,
    val customerId: String,
    val riderId: String,
    val pickedUpTime: LocalDateTime = LocalDateTime.now(),
    var deliveryCompletedTime: LocalDateTime? = null,
    @Version val version: Int = 0,
)