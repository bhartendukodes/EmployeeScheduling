import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.jetbrains.kotlin.jvm") version "1.5.31"
	id("org.jetbrains.kotlin.plugin.spring") version "1.5.31"
	java
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}


dependencies {
	// Kotlin standard library, adjusted for Java 17 compatibility
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// Spring Boot dependencies
	implementation(platform("ai.timefold.solver:timefold-solver-bom:1.8.0"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("ai.timefold.solver:timefold-solver-spring-boot-starter")

	// Swagger for API documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	// UI dependencies without webjar locator due to incompatibility issues
	runtimeOnly("ai.timefold.solver:timefold-solver-webui")
	runtimeOnly("org.webjars:bootstrap:5.2.3")
	runtimeOnly("org.webjars:jquery:3.6.0") // Version adjusted for availability
	runtimeOnly("org.webjars:font-awesome:5.15.1")
	runtimeOnly("org.webjars.npm:js-joda:1.11.0")

	// Testing libraries
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("org.awaitility:awaitility")
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs = listOf("-Xjsr305=strict")
	}
}
