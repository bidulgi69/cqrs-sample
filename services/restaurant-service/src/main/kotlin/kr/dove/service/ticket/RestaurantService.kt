package kr.dove.service.ticket

import core.restaurant.Restaurant
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RestaurantService {

    @PostMapping(
        value = ["/restaurant"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createRestaurant(@RequestBody restaurant: Restaurant): Mono<Restaurant>

    @GetMapping(
        value = ["/restaurants"],
        produces = ["application/x-ndjson"]
    )
    fun findAllRestaurants(): Flux<Restaurant>

    @GetMapping(
        value = ["/restaurant/{restaurantId}"],
        produces = ["application/json"]
    )
    fun getRestaurant(@PathVariable(name = "restaurantId") restaurantId: String): Mono<Restaurant>
}