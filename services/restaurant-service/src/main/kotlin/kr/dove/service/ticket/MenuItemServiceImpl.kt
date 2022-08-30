package kr.dove.service.ticket

import core.restaurant.MenuItem
import exceptions.RestaurantNotFoundException
import kr.dove.service.ticket.persistence.MenuItemEntity
import kr.dove.service.ticket.persistence.MenuItemRepository
import kr.dove.service.ticket.persistence.RestaurantRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class MenuItemServiceImpl(
    private val restaurantRepository: RestaurantRepository,
    private val menuItemRepository: MenuItemRepository,
) : MenuItemService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createMenu(menuItem: MenuItem): Mono<MenuItem> {
        return restaurantRepository.findById(menuItem.restaurantId)
            .switchIfEmpty(Mono.error(RestaurantNotFoundException("Invalid restaurant id.")))
            .flatMap {
                menuItemRepository.save(
                    MenuItemEntity(
                        menuItem.menuItemId,
                        menuItem.restaurantId,
                        menuItem.name,
                        menuItem.description,
                        menuItem.price,
                        menuItem.rating
                    )
                )
            }.flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    override fun findAllMenusByRestaurantId(restaurantId: String): Flux<MenuItem> {
        return menuItemRepository.findAllByRestaurantId(restaurantId)
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    private fun MenuItemEntity.cast(): MenuItem {
        return MenuItem(
            id,
            restaurantId,
            name,
            description,
            price,
            rating
        )
    }
}