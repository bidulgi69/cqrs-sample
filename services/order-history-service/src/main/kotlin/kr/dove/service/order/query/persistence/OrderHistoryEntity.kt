package kr.dove.service.order.query.persistence

import core.state.State
import core.values.CreditCard
import core.values.OrderItem
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Mapping

@Document(indexName = "histories")
@Mapping(mappingPath = "/elasticsearch/mappings/mappings.json")
data class OrderHistoryEntity(
    @Id val orderId: String,
    var state: State,
    val customerId: String,
    val restaurantId: String,
    var payment: CreditCard,
    var orderItems: List<OrderItem>,
    var ticketId: String? = null,
    var acceptTime: Long? = null,
    var readyTime: Long? = null,
    var preparingTime: Long? = null,
    var pickedUpTime: Long? = null,
    var deliveryCompletedTime: Long? = null,
)