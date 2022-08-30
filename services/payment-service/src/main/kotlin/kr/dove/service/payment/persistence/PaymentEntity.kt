package kr.dove.service.payment.persistence

import core.state.State
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(value = "PAYMENTS")
data class PaymentEntity(
    @Id val id: String,
    var state: State,
    val orderId: String,
    val customerId: String,
    val restaurantId: String,
    val paymentAsJson: String,
    @Version val version: Int = 0,
)