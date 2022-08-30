all: compile run test

bg:
	docker-compose up -d zookeeper kafka mysql elasticsearch

compile:
	chmod +x ./gradlew && \
	./gradlew :api:build && \
	./gradlew :services:order-service:build && \
	./gradlew :services:customer-service:build && \
	./gradlew :services:restaurant-service:build && \
	./gradlew :services:payment-service:build && \
	./gradlew :services:kitchen-service:build && \
	./gradlew :services:delivery-service:build && \
	./gradlew :services:order-history-service:build && \
	./gradlew :services:debezium-server:build && \
	docker-compose build

run: bg
	sleep 30 && \
	docker-compose up -d && \
	chmod +x scripts/health-check-servers.sh && scripts/health-check-servers.sh

test:
	chmod +x scripts/create-order-test.sh && scripts/create-order-test.sh

clean:
	docker-compose down --remove-orphans && \
	sleep 3 && \
	docker volume rm $$(docker volume ls -qf dangling=true) && \
	docker rmi $$(docker images | grep -e dove-cqrs)