package kr.dove.service.order.query

import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.xcontent.XContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import reactor.core.publisher.Mono

@SpringBootApplication
@ConfigurationProperties(prefix = "spring.data.elasticsearch.index")
class OrderQueryServiceApplication(
	private val reactiveElasticsearchClient: ReactiveElasticsearchClient,
) {
	private val logger: Logger = LoggerFactory.getLogger(this::class.java)

	lateinit var name: String
	lateinit var shards: String
	lateinit var replicas: String

	@Bean
	fun createIndex() {
		reactiveElasticsearchClient.indices().getIndex(GetIndexRequest(name))
			.onErrorResume { Mono.empty() } //  if index does not exist, move to switchIfEmpty function.
			.flatMap { logger.info("Index already exists. $name"); Mono.just(true) }
			.switchIfEmpty(Mono.defer {
				val createIndexRequest = CreateIndexRequest(name)
				createIndexRequest.settings(
					Settings.builder()
						.put("index.number_of_shards", shards)
						.put("index.number_of_replicas", replicas)
				)
				createIndexRequest.source("{\n" +
						"  \"settings\": {\n" +
						"  }, \n" +
						"\"mappings\": {\n" +
						"  \"properties\": {\n" +
						"    \"orderId\": {\n" +
						"      \"type\": \"text\"\n" +
						"    },\n" +
						"    \"state\": {\n" +
						"      \"type\": \"keyword\"\n" +
						"    },\n" +
						"    \"customerId\": {\n" +
						"      \"type\": \"text\"\n" +
						"    },\n" +
						"    \"restaurantId\": {\n" +
						"      \"type\": \"text\"\n" +
						"    },\n" +
						"    \"payment\": {\n" +
						"      \"properties\": {\n" +
						"        \"cvc\": {\n" +
						"          \"type\": \"text\"\n" +
						"        },\n" +
						"        \"number\": {\n" +
						"          \"type\": \"text\"\n" +
						"        },\n" +
						"        \"yy\": {\n" +
						"          \"type\": \"text\"\n" +
						"        },\n" +
						"        \"mm\": {\n" +
						"          \"type\": \"text\"\n" +
						"        }\n" +
						"      }\n" +
						"    },\n" +
						"    \"orderItems\": {\n" +
						"      \"properties\": {\n" +
						"        \"menuItemId\": {\n" +
						"          \"type\": \"keyword\"\n" +
						"        },\n" +
						"        \"quantity\": {\n" +
						"          \"type\": \"integer\"\n" +
						"        }\n" +
						"      }\n" +
						"    },\n" +
						"    \"ticketId\": {\n" +
						"      \"type\": \"text\"\n" +
						"    },\n" +
						"    \"acceptTime\": {\n" +
						"      \"type\": \"long\"\n" +
						"    },\n" +
						"    \"readyTime\": {\n" +
						"      \"type\": \"long\"\n" +
						"    },\n" +
						"    \"preparingTime\": {\n" +
						"      \"type\": \"long\"\n" +
						"    },\n" +
						"    \"pickedUpTime\": {\n" +
						"      \"type\": \"long\"\n" +
						"    },\n" +
						"    \"deliveryCompletedTime\": {\n" +
						"      \"type\": \"long\"\n" +
						"    }\n" +
						"  	}\n" +
						"  }\n" +
						"}\n", XContentType.JSON)
				reactiveElasticsearchClient
					.indices()
					.createIndex(createIndexRequest)
			}).subscribe { logger.info("Check Index($name) on start executed.") }
	}
}

fun main(args: Array<String>) {
	runApplication<OrderQueryServiceApplication>(*args)
}
