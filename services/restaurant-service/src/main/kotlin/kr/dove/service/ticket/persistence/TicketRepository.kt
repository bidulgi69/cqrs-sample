package kr.dove.service.ticket.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface TicketRepository : R2dbcRepository<TicketEntity, String> {
    fun findByOrderId(orderId: String): Mono<TicketEntity>
}