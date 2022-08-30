package kr.dove.service.order.command.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository

interface OrderRepository : R2dbcRepository<OrderEntity, String>