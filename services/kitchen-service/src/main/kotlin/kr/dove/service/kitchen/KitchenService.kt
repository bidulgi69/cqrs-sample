package kr.dove.service.kitchen

import core.order.Order
import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Mono

interface KitchenService {

    fun prepareFood(orderId: String, order: Order): Mono<Void>

    @GetMapping(
        value = ["/kitchen/ready"]
    )
    fun readyFood(): Mono<Void>

}