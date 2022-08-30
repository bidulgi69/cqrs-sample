package kr.dove.service.payment

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
    private val paymentService: PaymentService,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun orderMessageConsumer(): Consumer<Event<String, Order>> {
        return Consumer<Event<String, Order>> { (type, key, order, publishedAt) ->
            logger.info("Process message from topic 'order-payment' with type {} published at {}", type, publishedAt)
            when (type) {
                EventType.ORDER_CREATED -> {
                    val rand = Math.random() * 1
                    //  if you want to raise an error probability,
                    //  change the value of right side.
                    //  by default, probability: 0.0
                    if (rand > 1) {
                        paymentService.rejectPayment(key, order)
                            .subscribe()
                    } else {
                        paymentService.approvePayment(key, order)
                            .subscribe()
                    }
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