package kr.dove.service.debezium.server

import com.google.gson.Gson
import core.event.Event
import core.order.Order
import io.debezium.config.Configuration
import io.debezium.data.Envelope.FieldName
import io.debezium.data.Envelope.Operation
import io.debezium.embedded.Connect
import io.debezium.engine.DebeziumEngine
import io.debezium.engine.RecordChangeEvent
import io.debezium.engine.format.ChangeEventFormat
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.errors.DataException
import org.apache.kafka.connect.source.SourceRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class DebeziumListener(
    sourceConnector: Configuration,
    private val gson: Gson,
    private val kafkaTemplate: KafkaTemplate<String, Event<String, Order>>,
) : InitializingBean, DisposableBean {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val debeziumEngine: DebeziumEngine<RecordChangeEvent<SourceRecord>> = DebeziumEngine
        .create(ChangeEventFormat.of(Connect::class.java))
        .using(sourceConnector.asProperties())
        .notifying { event -> handleChangeEvent(event) }
        .build()

    private fun handleChangeEvent(sourceRecordChangeEvent: RecordChangeEvent<SourceRecord>) {
        val sourceRecord: SourceRecord = sourceRecordChangeEvent.record()
        logger.info("Key = ${sourceRecord.key()}, value = ${sourceRecord.value()}")

        try {
            with(sourceRecord.value() as Struct) {
                val code: String = try {
                    get(FieldName.OPERATION) as String
                } catch (e: DataException) {
                    //  when operation is not present.
                    return
                }

                val operation = Operation.forCode(code) ?. let { op ->
                    when (op) {
                        Operation.CREATE -> FieldName.AFTER
                        Operation.DELETE -> FieldName.BEFORE
                        Operation.READ -> return
                        else -> throw UnsupportedOperationException("Unsupported database operations :: $op")
                    }
                }
                val struct = get(operation) as Struct
                val eventId = struct.get("id") as Int
                val topic = struct.get("topic") as String
                val event = gson.fromJson(struct.get("event_as_json") as String, Event::class.java) as Event<String, Order>
                logger.info("Message: Id($eventId) To topic $topic with message::$event")

                kafkaTemplate.send(
                    topic,
                    UUID.randomUUID().toString(),
                    event
                )
            }
        } catch (e: RuntimeException) {
            //  throw error
            e.printStackTrace()
        }
    }

    override fun afterPropertiesSet() {
        debeziumEngine.run()
    }

    override fun destroy() {
        debeziumEngine.close()
    }
}