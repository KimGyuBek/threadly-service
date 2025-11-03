apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    toolVersion = "0.8.11"
}

tasks.named<Test>("test") {
    finalizedBy("jacocoTestReport")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("test"))

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/dto/**/*ApiResponse.class",
                    "**/dto/**/*Command.class",
                    "**/dto/**/*Query.class",
                    "**/dto/**/*Response.class",
                    "**/dto/**/*Request.class",
                    "**/metadata/**/*Meta.class",
                    "**/response/**/*.class",
                    "**/request/**/*Request.class",
                    "**/*Event.class",
                    "**/entity/**/*Entity.class",
                    "**/base/BaseEntity.class",
                    "**/*Type.class",
                    "**/*Status.class",
                    "**/ErrorCode.class",
                    "**/port/**/*Port.class",
                    "**/port/**/*UseCase.class",
                    "**/port/**/*Projection.class",
                    "**/config/**/*Config.class",
                    "**/properties/**/*Properties.class",
                    "**/*Application.class",
                    "**/exception/**/*.class",
                    "**/*Exception.class",
                    "**/mapper/**/*Mapper.class",
                    "**/repository/**/*Repository.class",
                    "**/*Module.class",
                    "**/filter/**/*.class",
                    "**/interceptor/**/*.class"
                )
            }
        })
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}
