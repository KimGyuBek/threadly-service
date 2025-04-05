dependencies {
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-commons"))
    implementation(project(":threadly-core:core-domain"))



    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-tx")
    implementation("org.flywaydb:flyway-core:11.5.0")
    implementation("org.flywaydb:flyway-database-postgresql")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")


}