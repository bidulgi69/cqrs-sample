package kr.dove.service.delivery

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@SpringBootApplication
class DeliveryServiceApplication {

	@Bean
	fun builder(): WebClient.Builder {
		return WebClient.builder()
	}

	@Bean
	fun gson(): Gson {
		return GsonBuilder()
			.setLenient()
			.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
			.create()
	}
}

fun main(args: Array<String>) {
	runApplication<DeliveryServiceApplication>(*args)
}
