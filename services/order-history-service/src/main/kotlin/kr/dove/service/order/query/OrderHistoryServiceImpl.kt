package kr.dove.service.order.query

import core.order.Order
import core.state.State
import exceptions.OrderNotFoundException
import kr.dove.service.order.query.persistence.OrderHistoryEntity
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@RestController
class OrderHistoryServiceImpl(
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchOperations,
) : OrderHistoryService {


    override fun getOrder(orderId: String): Mono<Order> {
        val queryBuilder = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("orderId", orderId))
            )
        return reactiveElasticsearchTemplate
            .search(queryBuilder.build(), OrderHistoryEntity::class.java)
            .elementAt(0)
            .doOnError(IndexOutOfBoundsException::class.java) {
                throw OrderNotFoundException("Invalid order id.")
            }
            .flatMap { hit ->
                Mono.just(
                    hit.cast()
                )
            }
    }

    override fun getOrdersByState(state: State): Flux<Order> {
        val queryBuilder = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("state", state))
            )
        return reactiveElasticsearchTemplate
            .search(queryBuilder.build(), OrderHistoryEntity::class.java)
            .flatMap { hit ->
                Mono.just(
                    hit.cast()
                )
            }
    }

    override fun findAllHistories(): Flux<Order> {
        return reactiveElasticsearchTemplate
            .search(NativeSearchQueryBuilder().build(), OrderHistoryEntity::class.java)
            .flatMap { hit ->
                Mono.just(
                    hit.cast()
                )
            }
    }

    override fun saveOrder(order: Order): Mono<Void> {
        return reactiveElasticsearchTemplate.save(
            OrderHistoryEntity(
                order.id,
                order.state,
                order.customerId,
                order.restaurantId,
                order.payment,
                order.orderItems,
                order.ticketId,
                order.acceptTime ?.toEpochSecond(ZoneOffset.UTC),
                order.readyTime ?.toEpochSecond(ZoneOffset.UTC),
                order.preparingTime,
                order.pickedUpTime ?.toEpochSecond(ZoneOffset.UTC),
                order.deliveryCompletedTime ?. toEpochSecond(ZoneOffset.UTC)
            )
        ).then()
    }

    private fun SearchHit<OrderHistoryEntity>.cast(): Order {
        return with(this.content) {
            Order(
                orderId,
                state,
                customerId,
                restaurantId,
                payment,
                orderItems,
                ticketId,
                acceptTime ?. let { LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.of("UTC")) },
                readyTime ?. let { LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.of("UTC")) },
                preparingTime,
                pickedUpTime ?. let { LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.of("UTC")) },
                deliveryCompletedTime ?. let { LocalDateTime.ofInstant(Instant.ofEpochSecond(it), ZoneId.of("UTC")) }
            )
        }
    }
}