package kr.dove.service.debezium.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime

@SpringBootApplication
class DebeziumServerApplication {

	@Bean
	fun gson(): Gson {
		return GsonBuilder()
			.setLenient()
			.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
			.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
			.create()
	}
}

fun main(args: Array<String>) {
	runApplication<DebeziumServerApplication>(*args)
}
