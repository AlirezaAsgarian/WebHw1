plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.cloud.tools.jib") version "3.2.0"
}

group = "ir.aa"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	// Lombok dependency
	compileOnly("org.projectlombok:lombok:1.18.28")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.projectlombok:lombok:1.18.28")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("org.postgresql:postgresql")
	testRuntimeOnly("com.h2database:h2") // Or the latest version available
	implementation("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
	implementation(project(":rest-dto-common"))
}

jib {
	container {
		args = listOf("--spring.config.additional-location=/etc/config/")
	}
	to {
		image = "authentication:1"
	}
}



tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	enabled = false
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	mainClass.set("ir.aa.weatherprediction.WeatherPredictionApplication")
}



tasks.withType<Test> {
	useJUnitPlatform()
}
