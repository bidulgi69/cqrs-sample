package kr.dove.service.order.query

import core.order.Order
import core.state.State
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderHistoryService {

    @GetMapping(
        value = ["/history/{orderId}"],
        produces = ["application/json"]
    )
    fun getOrder(@PathVariable(name = "orderId") orderId: String): Mono<Order>

    @GetMapping(
        value = ["/histories/{state}"],
        produces = ["application/x-ndjson"]
    )
    fun getOrdersByState(@PathVariable(name = "state") state: State): Flux<Order>

    @GetMapping(
        value = ["/histories"],
        produces = ["application/x-ndjson"]
    )
    fun findAllHistories(): Flux<Order>

    fun saveOrder(order: Order): Mono<Void>
}