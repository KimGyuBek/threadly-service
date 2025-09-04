dependencies {
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-core:core-domain"))
    implementation(project(":threadly-commons"))

    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-tx")
}