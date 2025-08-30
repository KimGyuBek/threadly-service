dependencies {
    implementation(project(":threadly-core:core-usecase"))
    implementation(project(":threadly-commons"))
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-core:core-service"))
    implementation(project(":threadly-adapters:adapter-http"))
    implementation(project(":threadly-adapters:adapter-persistence"))
    implementation(project(":threadly-adapters:adapter-redis"))
    implementation(project(":threadly-adapters:adapter-storage"))
    implementation(project(":threadly-adapters:adapter-kafka"))
    implementation(project(":threadly-core:core-domain:"))


    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("io.micrometer:micrometer-core")


    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")

}

val appMainClassName = "com.threadly.ThreadlyApplication"
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set(appMainClassName)
    archiveClassifier.set("boot")
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "test")
    systemProperty("file.encoding", "UTF-8")
    maxParallelForks = 1
}


