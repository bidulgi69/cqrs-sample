package kr.dove.service.payment.persistence

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface PaymentRepository : R2dbcRepository<PaymentEntity, String> {
    fun deleteByOrderId(orderId: String): Mono<Void>
}