package kr.dove.service.order.command.persistence

import core.state.State
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(value = "ORDERS")
data class OrderEntity(
    @Id val id: String,
    var state: State = State.PENDING,
    val customerId: String,
    val restaurantId: String,
    var paymentAsJson: String,
    var orderItemsAsJson: String,
    @Version val version: Int = 0
)