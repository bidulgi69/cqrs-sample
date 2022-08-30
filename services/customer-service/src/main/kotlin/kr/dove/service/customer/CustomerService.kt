package kr.dove.service.customer

import core.customer.Customer
import core.order.Order
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomerService {

    @GetMapping(
        value = ["/customers"],
        produces = ["application/x-ndjson"]
    )
    fun findAllCustomers(): Flux<Customer>

    @PostMapping(
        value = ["/customer"],
        consumes = ["application/json"],
        produces = ["application/json"]
    )
    fun createCustomer(@RequestBody customer: Customer): Mono<Customer>

    @GetMapping(
        value = ["/customer/{customerId}"],
        produces = ["application/json"]
    )
    fun getCustomer(@PathVariable(name = "customerId") customerId: String): Mono<Customer>

    fun verifyCustomer(orderId: String, order: Order): Mono<Void>
}