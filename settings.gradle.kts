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

include("threadly-adapters:adapter-persistence")
include("threadly-adapters:adapter-http")
include("threadly-adapters:adapter-redis")
include("threadly-adapters:adapter-storage")
include("threadly-adapters:adapter-kafka")

include("threadly-apps:app-api")
include("threadly-apps:app-batch")

include("threadly-commons")

include("threadly-core:core-service")
include("threadly-core:core-domain")
include("threadly-core:core-usecase")
include("threadly-core:core-port")
