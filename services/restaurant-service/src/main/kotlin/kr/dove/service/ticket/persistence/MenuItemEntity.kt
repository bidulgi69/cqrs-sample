package kr.dove.service.ticket.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(value = "MENUS")
data class MenuItemEntity(
    @Id val id: String,
    val restaurantId: String,
    var name: String,
    var description: String,
    var price: Int,
    var rating: Float = 0f,
    @Version val version: Int = 0,
)