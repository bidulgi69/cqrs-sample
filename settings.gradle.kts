
rootProject.name = "microservice-cqrs"
include(
    ":api",
    ":services:order-service",
    ":services:customer-service",
    ":services:restaurant-service",
    ":services:payment-service",
    ":services:kitchen-service",
    ":services:delivery-service",
    ":services:order-history-service",
    ":services:debezium-server"
)
