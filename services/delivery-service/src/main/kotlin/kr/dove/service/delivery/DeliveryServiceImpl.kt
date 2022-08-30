package kr.dove.service.delivery

import com.google.gson.Gson
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.state.State
import kr.dove.service.delivery.persistence.DeliveryEntity
import kr.dove.service.delivery.persistence.DeliveryRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.LocalDateTime
import java.util.*

@RestController
class DeliveryServiceImpl(
    private val deliveryRepository: DeliveryRepository,
    private val builder: WebClient.Builder,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    @Value("\${spring.profiles.active:default}") private val profile: String,
) : DeliveryService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val orderHistoryServiceHostname: String = if (profile == "docker") "history" else "localhost"

    @Transactional
    override fun pickedUp(): Flux<Void> {
        logger.info("Finding foods to deliver...")
        return getReadyStatedOrder()
            .flatMap { order ->
                Mono.zip(
                    deliveryRepository.save(
                        DeliveryEntity(
                            UUID.randomUUID().toString(),
                            State.PICKEDUP,
                            order.id,
                            order.customerId,
                            UUID.randomUUID().toString(),    //  random  rider id
                        )
                    ),
                    Mono.just(order)
                )
            }.flatMap { tuple ->
                val (deliveryEntity, order) = tuple
                r2dbcEntityTemplate.insert(
                    createMessage(
                        EventType.DELIVERY_PICKEDUP,
                        deliveryEntity.orderId,
                        order.apply {
                            this.pickedUpTime = deliveryEntity.pickedUpTime
                            this.state = State.PICKEDUP
                        }
                    )
                ).then()
            }
    }

    @Transactional
    override fun completeDelivery(): Flux<Void> {
        logger.info("Complete delivery service called...")
        return deliveryRepository.findAllByState(State.PICKEDUP)
            .flatMap { en ->
                Mono.zip(
                    deliveryRepository.save(
                        en.apply {
                            this.deliveryCompletedTime = LocalDateTime.now()
                            this.state = State.COMPLETED
                        }
                    ),
                    getOrder(en.orderId)
                )
            }.flatMap { tuple ->
                val (deliveryEntity, order) = tuple
                r2dbcEntityTemplate.insert(
                    createMessage(
                        EventType.DELIVERY_COMPLETED,
                        deliveryEntity.orderId,
                        order.apply {
                            this.deliveryCompletedTime = deliveryEntity.deliveryCompletedTime!!
                            this.state = State.COMPLETED
                        }
                    )
                ).then()
            }
    }

    private fun getReadyStatedOrder(): Flux<Order> {
        //  request to order-history-service
        return getClient()
            .get()
            .uri("$orderHistoryServiceHostname:8087/histories/READY")
            .retrieve()
            .bodyToFlux(Order::class.java)
    }

    private fun getOrder(orderId: String): Mono<Order> {
        //  request to order-history service
        return getClient()
            .get()
            .uri("$orderHistoryServiceHostname:8087/history/$orderId")
            .retrieve()
            .bodyToMono(Order::class.java)
    }

    private fun createMessage(eventType: EventType, key: String, data: Order): Outbox =
        Outbox(
            topic = "order-reply",
            eventAsJson = gson.toJson(
                Event(
                    eventType,
                    key,
                    data,
                )
            )
        )

    private fun getClient(): WebClient = builder.build()
}