package kr.dove.service.delivery

import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Flux

interface DeliveryService {

    @GetMapping(
        value = ["/delivery/pickedup"]
    )
    fun pickedUp(): Flux<Void>

    @GetMapping(
        value = ["/delivery/complete"]
    )
    fun completeDelivery(): Flux<Void>
}