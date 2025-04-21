dependencies{

    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-commons"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")


    testImplementation("it.ozimov:embedded-redis:0.7.2")
}