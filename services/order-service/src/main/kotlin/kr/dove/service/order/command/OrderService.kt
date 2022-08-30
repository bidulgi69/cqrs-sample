package kr.dove.service.order.command

import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

interface OrderService {

    @PostMapping(
        value = ["/order"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createOrder(@RequestBody order: Order): Mono<Order>

    @DeleteMapping(
        value = ["/order/{orderId}"]
    )
    fun deleteOrder(@PathVariable(name = "orderId") orderId: String): Mono<Void>

    @GetMapping(
        value = ["/order/{orderId}"],
        produces = ["application/json"]
    )
    fun getOrder(@PathVariable(name = "orderId") orderId: String): Mono<Order>

    fun approveOrder(orderId: String, order: Order): Mono<Void>

    fun prepareCooking(orderId: String, order: Order): Mono<Void>

    fun readyCooking(orderId: String, order: Order): Mono<Void>

    fun pickedUpDelivery(orderId: String, order: Order): Mono<Void>

    fun completedDelivery(orderId: String, order: Order): Mono<Void>

    fun <K, T> createMessage(bindingName: String, eventType: EventType, key: K, data: T): Outbox
}