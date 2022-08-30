package kr.dove.service.delivery.persistence

import core.state.State
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface DeliveryRepository : R2dbcRepository<DeliveryEntity, String> {
    fun findAllByState(state: State): Flux<DeliveryEntity>
}