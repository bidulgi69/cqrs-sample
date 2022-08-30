package kr.dove.service.ticket

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.state.State
import core.ticket.Ticket
import core.values.OrderItem
import exceptions.TicketNotFoundException
import kr.dove.service.ticket.persistence.TicketEntity
import kr.dove.service.ticket.persistence.TicketRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.*

@RestController
class TicketServiceImpl(
    private val ticketRepository: TicketRepository,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : TicketService {

    @Transactional
    override fun createTicket(orderId: String, order: Order): Mono<Void> {
        return ticketRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.defer {
                ticketRepository.save(
                    TicketEntity(
                        id = UUID.randomUUID().toString(),
                        orderId = orderId,
                        restaurantId = order.restaurantId,
                        orderItemsAsJson = gson.toJson(order.orderItems)
                    )
                )
            })
            .flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-reply",
                        EventType.TICKET_CREATED,
                        orderId,
                        order.apply {
                            this.ticketId = en.id
                        }
                    )
                )
            }
            .onErrorResume {
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-reply",
                        EventType.TICKET_REJECTED,
                        orderId,
                        order
                    )
                )
            }
            .then()
    }

    @Transactional
    override fun rejectTicket(orderId: String, order: Order): Mono<Void> {
        return ticketRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.error(TicketNotFoundException("Invalid order id.")))
            .flatMap { en ->
                ticketRepository.save(
                    en.apply {
                        this.state = State.REJECTED
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-reply",
                        EventType.TICKET_REJECTED,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    @Transactional
    override fun approveTicket(orderId: String, order: Order): Mono<Void> {
        return ticketRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.error(TicketNotFoundException("Invalid order id.")))
            .flatMap { en ->
                ticketRepository.save(
                    en.apply {
                        this.state = State.ACCEPTED
                        //  order history will consume this event.
                        this.acceptTime = LocalDateTime.now()
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-reply",
                        EventType.TICKET_APPROVED,
                        orderId,
                        order.apply {
                            this.acceptTime = en.acceptTime!!
                        }
                    )
                )
            }.then()
    }

    override fun getTicket(ticketId: String): Mono<Ticket> {
        return ticketRepository.findById(ticketId)
            .switchIfEmpty(Mono.error(TicketNotFoundException("Invalid ticket id.")))
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    override fun updateTicketState(orderId: String, state: State): Mono<Void> {
        return ticketRepository.findByOrderId(orderId)
            .switchIfEmpty(Mono.error(TicketNotFoundException("Invalid ticket id.")))
            .flatMap { en ->
                ticketRepository.save(
                    en.apply {
                        this.state = state
                    }
                )
            }.then()
    }

    fun <K, T> createMessage(bindingName: String, eventType: EventType, key: K, data: T): Outbox =
        Outbox(
            topic = bindingName,
            eventAsJson = gson.toJson(
                Event(
                    eventType,
                    key,
                    data,
                )
            )
        )

    private fun TicketEntity.cast(): Ticket {
        return Ticket(
            id,
            state,
            orderId,
            restaurantId,
            acceptTime,
            readyTime,
            preparingTime,
            pickedUpTime,
            gson.fromJson(orderItemsAsJson, object : TypeToken<List<OrderItem>>(){}.type),
        )
    }
}