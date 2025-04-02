dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-tx")

    implementation("org.flywaydb:flyway-core:11.5.0")
//    implementation("org.flywaydb:flyway-mysql")
    implementation("org.flywaydb:flyway-database-postgresql")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    runtimeOnly("com.mysql:mysql-connector-j")

}