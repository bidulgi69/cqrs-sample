package kr.dove.service.ticket.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository

interface RestaurantRepository : R2dbcRepository<RestaurantEntity, String>