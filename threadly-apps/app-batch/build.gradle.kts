import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.apache.commons:commons-csv:1.10.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus-simpleclient:1.13.6")
    implementation("io.prometheus:simpleclient_pushgateway:0.16.0")


}
tasks.getByName<BootJar>("bootJar"){
    enabled = false


}