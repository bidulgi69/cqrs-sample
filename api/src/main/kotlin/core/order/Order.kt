package core.order

import com.fasterxml.jackson.annotation.JsonProperty
import core.state.State
import core.values.CreditCard
import core.values.OrderItem
import java.time.LocalDateTime

data class Order(
    @field:JsonProperty("id") var id: String,
    @field:JsonProperty("state") var state: State = State.PENDING,
    @field:JsonProperty("customerId") val customerId: String,
    @field:JsonProperty("restaurantId") val restaurantId: String,
    @field:JsonProperty("payment") var payment: CreditCard,
    @field:JsonProperty("orderItems") var orderItems: List<OrderItem> = listOf(),

    //  ticket infos
    @field:JsonProperty("ticketId") var ticketId: String? = null,
    @field:JsonProperty("acceptTime") var acceptTime: LocalDateTime? = null,
    @field:JsonProperty("readyTime") var readyTime: LocalDateTime? = null,
    @field:JsonProperty("preparingTime") var preparingTime: Long? = null,
    @field:JsonProperty("pickedUpTime") var pickedUpTime: LocalDateTime? = null,
    @field:JsonProperty("deliveryCompletedTime") var deliveryCompletedTime: LocalDateTime? = null
) {
    constructor(): this(
        id = "",
        customerId = "",
        restaurantId = "",
        payment = CreditCard(),
    )
}