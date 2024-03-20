import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

val timefoldSolverVersion = "1.8.0"

dependencies {
	implementation(platform("ai.timefold.solver:timefold-solver-bom:$timefoldSolverVersion"))

	// Specifying dependencies, the version will be managed by the BOM
	implementation("ai.timefold.solver:timefold-solver-core")
	implementation("ai.timefold.solver:timefold-solver-spring-boot-starter")
	testImplementation("ai.timefold.solver:timefold-solver-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.springframework.boot:spring-boot-starter-web")



}

dependencyManagement {
	imports {
		// Use the variable directly here as well
		mavenBom("ai.timefold.solver:timefold-solver-bom:$timefoldSolverVersion")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
