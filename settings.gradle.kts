rootProject.name = "threadly"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://maven.springframework,org/release")
        }
        maven {
            url = uri("https://maven.restlet.com")
        }
    }
}