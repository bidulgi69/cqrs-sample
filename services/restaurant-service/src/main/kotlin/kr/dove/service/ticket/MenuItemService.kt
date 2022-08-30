package kr.dove.service.ticket

import core.restaurant.MenuItem
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MenuItemService {

    @PostMapping(
        value = ["/menu"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createMenu(@RequestBody menuItem: MenuItem): Mono<MenuItem>

    @GetMapping(
        value = ["/menus/{restaurantId}"],
        produces = ["application/x-ndjson"]
    )
    fun findAllMenusByRestaurantId(@PathVariable(name = "restaurantId") restaurantId: String): Flux<MenuItem>
}