plugins {
	java
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "6.21.0"
	checkstyle
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

spotless {
	java {
		googleJavaFormat("1.17.0")
		target("src/**/*.java")
		indentWithTabs(2)
		indentWithSpaces(4)
		trimTrailingWhitespace()
		endWithNewline()
	}
}

checkstyle {
	toolVersion = "10.15.0"
	configDirectory.set(file("config/checkstyle"))
}

jacoco {
	toolVersion = "0.8.11"
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
//	implementation("com.openai:openai-java:3.5.3")
	implementation("com.openai:openai-java-spring-boot-starter:3.5.3")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-core")
	mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
	testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-javaagent:${mockitoAgent.asPath}")
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		csv.required = false
		html.required = true
	}
}

tasks.withType<Checkstyle> {
	reports {
		xml.required.set(false)
		html.required.set(true)
	}
}
tasks.check {
	dependsOn("checkstyleMain", "checkstyleTest")
}