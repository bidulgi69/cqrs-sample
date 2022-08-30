package kr.dove.service.order.query

import core.event.Event
import core.event.EventType
import core.order.Order
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class MessageConsumerConfiguration(
    private val orderHistoryService: OrderHistoryService,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun orderMessageConsumer(): Consumer<Event<String, Order>> {
        return Consumer<Event<String, Order>> { (type, _, order, publishedAt) ->
            logger.info("Process message from topic 'order-reply' with type {} published at {}", type, publishedAt)
            when (type) {
                EventType.TICKET_CREATED,
                EventType.TICKET_APPROVED,
                EventType.KITCHEN_PREPARING,
                EventType.KITCHEN_READY,
                EventType.DELIVERY_PICKEDUP,
                EventType.DELIVERY_COMPLETED -> {
                    orderHistoryService.saveOrder(order)
                        .subscribe()
                }
                else -> {
                    //  do nothing
                }
            }
        }
    }
}