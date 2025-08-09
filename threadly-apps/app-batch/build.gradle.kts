import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    // Spring Batch 핵심만
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // 필요한 경우에만 외부 모듈 추가
    // implementation(project(":threadly-core:core-domain"))  // 필요시에만
    // implementation(project(":threadly-commons"))          // 필요시에만
    
    // DB 연결
    runtimeOnly("org.postgresql:postgresql")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = true
    archiveFileName.set("app-batch.jar")
}