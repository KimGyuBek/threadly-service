dependencies {
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-commons"))
    
    // Spring Cloud Stream Kafka
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    
    // Spring Boot 기본 의존성
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-tx")
}