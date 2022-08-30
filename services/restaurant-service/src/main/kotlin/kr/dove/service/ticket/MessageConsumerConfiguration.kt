package kr.dove.service.ticket

import core.event.Event
import core.event.EventType
import core.order.Order
import core.state.State
import exceptions.UnsupportedEventTypeException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class MessageConsumerConfiguration(
    private val ticketService: TicketService,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun orderMessageConsumer(): Consumer<Event<String, Order>> {
        return Consumer<Event<String, Order>> { (type, key, order, publishedAt) ->
            logger.info("Process message from topic 'order-ticket' with type {} published at {}", type, publishedAt)
            when (type) {
                EventType.ORDER_CREATED -> {
                    ticketService.createTicket(key, order)
                        .subscribe()
                }
                EventType.ORDER_REJECTED -> {
                    ticketService.rejectTicket(key, order)
                        .subscribe()
                }
                EventType.ORDER_APPROVED -> {
                    ticketService.approveTicket(key, order)
                        .subscribe()
                }
                EventType.KITCHEN_PREPARING -> {
                    ticketService.updateTicketState(key, State.PREPARING)
                        .subscribe()
                }
                EventType.KITCHEN_READY -> {
                    ticketService.updateTicketState(key, State.READY)
                        .subscribe()
                }
                EventType.DELIVERY_PICKEDUP -> {
                    ticketService.updateTicketState(key, State.PICKEDUP)
                        .subscribe()
                }
                EventType.DELIVERY_COMPLETED -> {
                    ticketService.updateTicketState(key, State.COMPLETED)
                        .subscribe()
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