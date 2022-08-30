package kr.dove.service.payment

import core.order.Order
import reactor.core.publisher.Mono

interface PaymentService {

    fun approvePayment(orderId: String, order: Order): Mono<Void>

    fun rejectPayment(orderId: String, order: Order): Mono<Void>

}