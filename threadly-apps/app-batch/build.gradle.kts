import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // or JDBC만 쓰면


    implementation(project(":threadly-adapters:adapter-persistence"))
    implementation(project(":threadly-core:core-domain"))
    implementation(project(":threadly-commons"))

    implementation("org.apache.commons:commons-csv:1.10.0")


    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    archiveFileName.set("app-batch.jar")
}