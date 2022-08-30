package kr.dove.service.kitchen

import core.event.Event
import core.event.EventType
import core.order.Order
import exceptions.UnsupportedEventTypeException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Consumer

@Configuration
class MessageConsumerConfiguration(
    private val kitchenService: KitchenService
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun orderMessageConsumer(): Consumer<Event<String, Order>> {
        return Consumer<Event<String, Order>> { (type, key, order, publishedAt) ->
            logger.info("Process message from topic 'order-kitchen' with type {} published at {}", type, publishedAt)
            when (type) {
                EventType.ORDER_APPROVED -> {
                    kitchenService.prepareFood(key, order)
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