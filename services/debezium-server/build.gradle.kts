plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
}

dependencies {
	val debeziumVersion = "2.0.0.Beta1"
	implementation(project(":api"))

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.google.code.gson:gson:${Version.gsonVersion}")
	// https://mvnrepository.com/artifact/io.debezium/debezium-connector-mysql
	implementation("io.debezium:debezium-connector-mysql:$debeziumVersion")
	// https://mvnrepository.com/artifact/io.debezium/debezium-embedded
	implementation("io.debezium:debezium-embedded:$debeziumVersion")
	implementation("org.springframework.kafka:spring-kafka")

	testImplementation("org.springframework.cloud:spring-cloud-stream::test-binder")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<Jar>("jar") {
	enabled = false
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.2")
	}
}