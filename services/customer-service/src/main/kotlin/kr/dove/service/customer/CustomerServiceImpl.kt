package kr.dove.service.customer

import com.google.gson.Gson
import core.customer.Customer
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.values.CreditCard
import core.values.Location
import exceptions.CustomerNotFoundException
import kr.dove.service.customer.persistence.CustomerEntity
import kr.dove.service.customer.persistence.CustomerRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : CustomerService {

    override fun findAllCustomers(): Flux<Customer> {
        return customerRepository.findAll()
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    override fun createCustomer(customer: Customer): Mono<Customer> {
        return customerRepository.save(
            CustomerEntity(
                customer.customerId,
                customer.firstname,
                customer.lastname,
                customer.fullname,
                gson.toJson(customer.address),
                gson.toJson(customer.card),
            )
        ).flatMap { en ->
            Mono.just(
                en.cast()
            )
        }
    }

    override fun getCustomer(customerId: String): Mono<Customer> {
        return customerRepository.findById(customerId)
            .switchIfEmpty(Mono.error(CustomerNotFoundException("Invalid customer id.")))
            .flatMap { en ->
                Mono.just(
                    en.cast()
                )
            }
    }

    @Transactional
    override fun verifyCustomer(orderId: String, order: Order): Mono<Void> {
        return customerRepository.existsById(order.customerId)
            .flatMap { exists ->
                r2dbcEntityTemplate.insert(
                    if (exists) {
                        createMessage(
                            EventType.CUSTOMER_APPROVED,
                            orderId,
                            order
                        )

                    } else {
                        createMessage(
                            EventType.CUSTOMER_REJECTED,
                            orderId,
                            order
                        )
                    }
                )
            }.then()
    }

    private fun createMessage(eventType: EventType, key: String, order: Order): Outbox =
        Outbox(
            topic = "order-reply",
            eventAsJson = gson.toJson(
                Event(
                    eventType,
                    key,
                    order,
                )
            )
        )

    private fun CustomerEntity.cast(): Customer {
        return Customer(
            id,
            firstname,
            lastname,
            fullname,
            gson.fromJson(addressAsJson, Location::class.java),
            gson.fromJson(cardAsJson, CreditCard::class.java)
        )
    }
}