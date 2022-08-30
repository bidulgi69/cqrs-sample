package kr.dove.service.ticket

import core.order.Order
import core.state.State
import core.ticket.Ticket
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Mono

interface TicketService {

    fun createTicket(orderId: String, order: Order): Mono<Void>

    fun rejectTicket(orderId: String, order: Order): Mono<Void>

    fun approveTicket(orderId: String, order: Order): Mono<Void>

    @GetMapping(
        value = ["/ticket/{ticketId}"],
        produces = ["application/json"]
    )
    fun getTicket(@PathVariable(name = "ticketId") ticketId: String): Mono<Ticket>

    fun updateTicketState(orderId: String, state: State): Mono<Void>
}