package kr.dove.service.ticket.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table

@Table(value = "RESTAURANTS")
data class RestaurantEntity(
    @Id val id: String,
    var name: String,
    var addressAsJson: String,
    @Version val version: Int = 0,
)
