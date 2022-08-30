package kr.dove.service.order.command

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@ConfigurationProperties(prefix = "spring.r2dbc")
class R2dbcConfiguration : AbstractR2dbcConfiguration() {

    lateinit var url: String
    lateinit var username: String
    lateinit var password: String

    override fun connectionFactory(): ConnectionFactory = orderConnectionFactory()

    @Bean
    fun orderConnectionFactory(): ConnectionFactory = generateConnectionFactory(url, username, password)

    @Bean
    fun ddlInitializer(): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory())
        initializer.setDatabasePopulator(
            ResourceDatabasePopulator(
                ClassPathResource("ddl.sql")
            )
        )

        return initializer
    }

    private fun generateConnectionFactory(url: String, username: String, password: String): ConnectionFactory {
        return ConnectionFactories.get("r2dbc:mysql://$username:$password@$url")
    }
}