package kr.dove.service.debezium.server

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import core.event.Event
import core.order.Order
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component

@Component
class MessageSerializer : Serializer<Event<String, Order>> {

    private val mapper = ObjectMapper()
        .registerModule(JavaTimeModule())

    override fun serialize(topic: String, data: Event<String, Order>): ByteArray {
        try {
            return mapper.writeValueAsBytes(data)
        } catch (e: JsonProcessingException) {
            throw SerializationException(e)
        }
    }
}