package kr.dove.service.payment

import com.google.gson.Gson
import core.event.Event
import core.event.EventType
import core.order.Order
import core.outbox.Outbox
import core.state.State
import kr.dove.service.payment.persistence.PaymentEntity
import kr.dove.service.payment.persistence.PaymentRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.util.*

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val gson: Gson,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) : PaymentService {

    @Transactional
    override fun approvePayment(orderId: String, order: Order): Mono<Void> {
        return paymentRepository.save(
            PaymentEntity(
                UUID.randomUUID().toString(),
                State.ACCEPTED,
                orderId,
                order.customerId,
                order.restaurantId,
                gson.toJson(order.payment),
            )
        ).flatMap {
            r2dbcEntityTemplate.insert(
                createMessage(
                    EventType.PAYMENT_APPROVED,
                    orderId,
                    order
                )
            )
        }
            .then()
            .onErrorResume {
                rejectPayment(orderId, order)
            }
    }

    @Transactional
    override fun rejectPayment(orderId: String, order: Order): Mono<Void> {
        return paymentRepository.deleteByOrderId(orderId)
            .then(Mono.defer {
                paymentRepository.save(
                    PaymentEntity(
                        UUID.randomUUID().toString(),
                        State.REJECTED,
                        orderId,
                        order.customerId,
                        order.restaurantId,
                        gson.toJson(order.payment)
                    )
                ).flatMap {
                    r2dbcEntityTemplate.insert(
                        createMessage(
                            EventType.PAYMENT_REJECTED,
                            orderId,
                            order
                        )
                    )
                }.then()
            })
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
}