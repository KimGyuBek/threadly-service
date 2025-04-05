dependencies {


    implementation(project(":threadly-core:core-usecase"))
    implementation(project(":threadly-core:core-port"))
    implementation(project(":threadly-commons"))
    implementation(project(":threadly-core:core-domain"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.springframework:spring-tx")

    implementation("io.jsonwebtoken:jjwt-api")
    implementation("io.jsonwebtoken:jjwt-impl")
    implementation("io.jsonwebtoken:jjwt-jackson")


}