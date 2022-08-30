package kr.dove.service.ticket.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface MenuItemRepository : R2dbcRepository<MenuItemEntity, String> {
    fun findAllByRestaurantId(restaurantId: String): Flux<MenuItemEntity>
}