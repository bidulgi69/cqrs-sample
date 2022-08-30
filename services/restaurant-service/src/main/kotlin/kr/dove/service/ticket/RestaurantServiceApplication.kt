package kr.dove.service.ticket

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime

@SpringBootApplication
class RestaurantServiceApplication {

	@Bean
	fun gson(): Gson {
		return GsonBuilder()
			.setLenient()
			.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
			.create()
	}
}

fun main(args: Array<String>) {
	runApplication<RestaurantServiceApplication>(*args)
}
