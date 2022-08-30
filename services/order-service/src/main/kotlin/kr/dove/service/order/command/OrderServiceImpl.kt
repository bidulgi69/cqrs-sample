package kr.dove.service.order.command

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.state.State
import core.values.CreditCard
import core.values.OrderItem
import exceptions.OrderNotFoundException
import kr.dove.service.order.command.persistence.OrderEntity
import kr.dove.service.order.command.persistence.OrderRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : OrderService {

    @Transactional
    override fun createOrder(order: Order): Mono<Order> {
        return orderRepository.save(
            OrderEntity(
                id = order.id,
                customerId = order.customerId,
                restaurantId = order.restaurantId,
                paymentAsJson = gson.toJson(order.payment),
                orderItemsAsJson = gson.toJson(order.orderItems),
            )
        ).flatMap { en ->
            r2dbcEntityTemplate.insert(
                createMessage(
                    "order-customer",
                    EventType.ORDER_CREATED,
                    order.id,
                    order
                )
            )
        }.then(
            Mono.just(
                order
            )
        )
    }

    override fun deleteOrder(orderId: String): Mono<Void> {
        return orderRepository.findById(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.REJECTED
                    }
                )
            }.then()
    }

    override fun getOrder(orderId: String): Mono<Order> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    @Transactional
    override fun approveOrder(orderId: String, order: Order): Mono<Void> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.ACCEPTED
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-kitchen",
                        EventType.ORDER_APPROVED,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    @Transactional
    override fun prepareCooking(orderId: String, order: Order): Mono<Void> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.PREPARING
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-ticket",
                        EventType.KITCHEN_PREPARING,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    @Transactional
    override fun readyCooking(orderId: String, order: Order): Mono<Void> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.READY
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-ticket",
                        EventType.KITCHEN_READY,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    @Transactional
    override fun pickedUpDelivery(orderId: String, order: Order): Mono<Void> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.PICKEDUP
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-ticket",
                        EventType.DELIVERY_PICKEDUP,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    @Transactional
    override fun completedDelivery(orderId: String, order: Order): Mono<Void> {
        return getOrderEntity(orderId)
            .flatMap { en ->
                orderRepository.save(
                    en.apply {
                        this.state = State.COMPLETED
                    }
                )
            }.flatMap { en ->
                r2dbcEntityTemplate.insert(
                    createMessage(
                        "order-ticket",
                        EventType.DELIVERY_COMPLETED,
                        orderId,
                        order.apply {
                            this.state = en.state
                        }
                    )
                )
            }.then()
    }

    private fun getOrderEntity(orderId: String): Mono<OrderEntity> =
        orderRepository.findById(orderId)
            .switchIfEmpty(Mono.error(OrderNotFoundException("Invalid order id.")))

    override fun <K, T> createMessage(bindingName: String, eventType: EventType, key: K, data: T): Outbox =
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

    private fun OrderEntity.cast(): Order = Order(
        id,
        state,
        customerId,
        restaurantId,
        gson.fromJson(paymentAsJson, CreditCard::class.java),
        gson.fromJson(orderItemsAsJson, object : TypeToken<List<OrderItem>>(){}.type)
    )
}