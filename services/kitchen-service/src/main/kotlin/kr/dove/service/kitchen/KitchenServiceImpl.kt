package kr.dove.service.kitchen

import com.google.gson.Gson
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.state.State
import kr.dove.service.kitchen.persistence.CookEntity
import kr.dove.service.kitchen.persistence.CookRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@RestController
class KitchenServiceImpl(
    private val cookRepository: CookRepository,
    private val builder: WebClient.Builder,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    @Value("\${spring.profiles.active:default}") private val profile: String,
    ) : KitchenService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val orderHistoryServiceHostname: String = if (profile == "docker") "history" else "localhost"

    @Transactional
    override fun prepareFood(orderId: String, order: Order): Mono<Void> {
        return cookRepository.save(
            CookEntity(
                UUID.randomUUID().toString(),
                State.PREPARING,
                orderId,
                order.ticketId!!,
                LocalDateTime.now(),
            )
        ).flatMap {
            r2dbcEntityTemplate.insert(
                createMessage(
                    EventType.KITCHEN_PREPARING,
                    orderId,
                    order.apply {
                        this.state = State.PREPARING
                    }
                )
            )
        }.then()
    }

    @Transactional
    override fun readyFood(): Mono<Void> {
        logger.info("Foods are ready to deliver!")
        return cookRepository.findOneByState(State.PREPARING)
            .flatMap { en ->
                Mono.zip(
                    cookRepository.save(
                        en.apply {
                            this.state = State.READY
                            this.readyTime = LocalDateTime.now()
                        }
                    ),
                    getOrder(en.orderId)
                )
            }.flatMap { tuple ->
                val (cookEntity, order) = tuple
                val gap = Duration.between(order.acceptTime!!, cookEntity.readyTime)
                r2dbcEntityTemplate.insert(
                    createMessage(
                        EventType.KITCHEN_READY,
                        cookEntity.orderId,
                        order.apply {
                            this.preparingTime = gap.toSeconds()
                            this.readyTime = cookEntity.readyTime
                            this.state = State.READY
                        }
                    )
                )
            }.then()
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