package kr.dove.service.ticket

import com.google.gson.Gson
import core.restaurant.Restaurant
import core.values.Location
import exceptions.RestaurantNotFoundException
import kr.dove.service.ticket.persistence.RestaurantEntity
import kr.dove.service.ticket.persistence.RestaurantRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class RestaurantServiceImpl(
    private val restaurantRepository: RestaurantRepository,
    private val gson: Gson,
) : RestaurantService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createRestaurant(restaurant: Restaurant): Mono<Restaurant> {
        return restaurantRepository.save(
            RestaurantEntity(
                restaurant.restaurantId,
                restaurant.name,
                gson.toJson(restaurant.address),
            )
        ).flatMap { en ->
            Mono.just(
                en.cast()
            )
        }
    }

    override fun findAllRestaurants(): Flux<Restaurant> {
        return restaurantRepository.findAll()
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    override fun getRestaurant(restaurantId: String): Mono<Restaurant> {
        return restaurantRepository.findById(restaurantId)
            .switchIfEmpty(Mono.error(RestaurantNotFoundException("Invalid restaurant id.")))
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    private fun RestaurantEntity.cast(): Restaurant {
        return Restaurant(
            id,
            name,
            gson.fromJson(addressAsJson, Location::class.java)
        )
    }
}