dependencies {
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-commons"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    /*mail*/
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}