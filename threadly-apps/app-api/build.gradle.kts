dependencies {
    implementation(project(":threadly-core:core-usecase"))
    implementation(project(":threadly-commons"))


    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-core:core-service"))
    implementation(project(":threadly-adapters:adapter-http"))
    implementation(project(":threadly-adapters:adapter-persistence"))
    implementation(project(":threadly-adapters:adapter-redis"))
    implementation(project(":threadly-core:core-domain:"))


    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-tx")


    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("org.springframework.boot:spring-boot-devtools")

    /*Jwt*/
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

}

val appMainClassName = "com.threadly.ThreadlyApplication"
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set(appMainClassName)
    archiveClassifier.set("boot")
}

tasks.withType<Test> {
    systemProperty("spring.profiles.active", "test")
    systemProperty("file.encoding", "UTF-8")
}