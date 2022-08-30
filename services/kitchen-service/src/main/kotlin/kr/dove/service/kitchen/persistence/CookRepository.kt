package kr.dove.service.kitchen.persistence

import core.state.State
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface CookRepository : R2dbcRepository<CookEntity, String> {
    @Query(value = "select * from order_db.COOKS where state = :1 order by accept_time asc limit 1")
    fun findOneByState(state: State): Mono<CookEntity>
}