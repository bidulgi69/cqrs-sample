package kr.dove.service.order.command

import core.event.Event
import core.event.EventType
import core.order.Order
import exceptions.UnsupportedEventTypeException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.util.function.Consumer

@Configuration
class MessageConsumerConfiguration(
    private val orderService: OrderService,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun orderReplyMessageConsumer(): Consumer<Event<String, Order>> {
        return Consumer<Event<String, Order>> { (type, key, order, publishedAt) ->
            logger.info("Process message from topic 'order-reply' with type {} published at {}", type, publishedAt)
            when (type) {
                EventType.CUSTOMER_APPROVED -> {
                    //  send message to ticket service
                    r2dbcEntityTemplate.insert(
                        orderService.createMessage(
                            "order-ticket",
                            EventType.ORDER_CREATED,
                            key,
                            order
                        )
                    ).subscribe()
                }
                EventType.CUSTOMER_REJECTED -> {
                    //  don't need to send deletion message to order-history service
                    //  order-history entity will be inserted after ticket entity created.
                    orderService.deleteOrder(key)
                        .subscribe()
                }
                EventType.TICKET_CREATED -> {
                    //  send message to payment service
                    r2dbcEntityTemplate.insert(
                        orderService.createMessage(
                            "order-payment",
                            EventType.ORDER_CREATED,
                            key,
                            order
                        )
                    ).subscribe()
                }
                EventType.TICKET_REJECTED -> {
                    orderService.deleteOrder(key)
                        .subscribe()
                }
                EventType.TICKET_APPROVED -> {
                    //  send message to kitchen service
                    orderService.approveOrder(
                        key, order
                    ).subscribe()
                }
                EventType.PAYMENT_APPROVED -> {
                    //  send message to ticket service
                    r2dbcEntityTemplate.insert(
                        orderService.createMessage(
                            "order-ticket",
                            EventType.ORDER_APPROVED,
                            key,
                            order
                        )
                    ).subscribe()
                }
                EventType.PAYMENT_REJECTED -> {
                    //  send message to ticket service
                    r2dbcEntityTemplate.insert(
                        orderService.createMessage(
                            "order-ticket",
                            EventType.ORDER_REJECTED,
                            key,
                            order
                        )
                    ).subscribe()
                }
                EventType.KITCHEN_PREPARING -> {
                    //  send message to ticket service
                    orderService.prepareCooking(
                        key, order
                    ).subscribe()
                }
                EventType.KITCHEN_READY -> {
                    orderService.readyCooking(
                        key, order
                    ).subscribe()
                }
                EventType.DELIVERY_PICKEDUP -> {
                    //  send message to ticket service
                    orderService.pickedUpDelivery(
                        key, order
                    ).subscribe()
                }
                EventType.DELIVERY_COMPLETED -> {
                    //  send message to ticket service
                    orderService.completedDelivery(
                        key, order
                    ).subscribe()
                }
                else -> {
                    val errorMsg = "Event Type $type is not supported."
                    logger.error(errorMsg)
                    throw UnsupportedEventTypeException(errorMsg)
                }
            }
        }
    }
}