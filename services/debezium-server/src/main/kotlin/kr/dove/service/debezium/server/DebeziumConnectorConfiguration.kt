package kr.dove.service.debezium.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Paths
import java.util.*

@Configuration
class DebeziumConnectorConfiguration(
    @Value("\${debezium.mysql.hostname}") private val hostname: String,
    @Value("\${debezium.mysql.port}") private val port: String,
    @Value("\${debezium.mysql.username}") private val username: String,
    @Value("\${debezium.mysql.password}") private val password: String,
    @Value("\${debezium.mysql.database}") private val database: String,
    @Value("\${spring.profiles.active:default}") private val profile: String,
) {

    @Bean
    fun sourceConnector(): io.debezium.config.Configuration {
        val pwd: String = if (profile == "default") Paths.get("").toAbsolutePath().toString() else ""
        return io.debezium.config.Configuration
            .create()
            .with("name", "mysql-outbox-connector")
            .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
            .with("database.server.id", "${10000 + (Math.random() * 500).toInt()}")
            .with("database.hostname", hostname)
            .with("database.port", port)
            .with("database.user", username)
            .with("database.password", password)
            .with("database.dbname", database)
            .with("database.include.list", database)
            //  The table name to be excluded or included is written as follows: schema_table
            .with("table.include.list", "order_db.outbox")
            //  .with("table.exclude.list", "order_db.orders,order_db.customers,order_db.deliveries,order_db.cooks,order_db.payments,order_db.tickets,order_db.restaurants,order_db.menus")
            .with("database.server.name", "database.mysql.server-${UUID.randomUUID().toString().substring(0, 4)}")
            .with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", "$pwd/data/offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            .build()
    }
}