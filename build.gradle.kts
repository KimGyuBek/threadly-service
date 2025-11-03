import com.linecorp.support.project.multi.recipe.configureByLabels

plugins {
    id("java")
    id("io.spring.dependency-management") version Versions.springDependencyManagementPlugin apply false
    id("org.springframework.boot") version Versions.springBoot apply false
    id("io.freefair.lombok") version Versions.lombokPlugin apply false
    id("com.coditory.integration-test") version Versions.integrationTestPlugin apply false
    id("com.epages.restdocs-api-spec") version Versions.restdocsApiSpec apply false
    id("org.asciidoctor.jvm.convert") version Versions.asciidoctorPlugin apply false
    id("com.linecorp.build-recipe-plugin") version Versions.lineRecipePlugin
}



allprojects {
    group = "com.threadly"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.restlet.com") }
        maven { url = uri("https://jitpack.io") }
    }
}

// Coverage Summary Task
tasks.register("printCoverageSummary") {
    group = "verification"
    description = "Print module-by-module coverage summary to console"

    val modules = listOf(
        "threadly-core:core-domain",
        "threadly-core:core-service",
        "threadly-core:core-port",
        "threadly-commons",
        "threadly-adapters:adapter-persistence",
        "threadly-adapters:adapter-redis",
        "threadly-adapters:adapter-storage",
        "threadly-adapters:adapter-kafka",
        "threadly-apps:app-api",
        "threadly-apps:app-batch"
    )

    doLast {
        println("\n" + "=".repeat(100))
        println("CODE COVERAGE SUMMARY (BY MODULE)")
        println("=".repeat(100))
        println(String.format("%-25s %12s %12s %12s %12s %12s",
            "MODULE", "INSTRUCTION", "BRANCH", "LINE", "METHOD", "CLASS"))
        println("-".repeat(100))

        modules.forEach { modulePath ->
            val module = project.findProject(":$modulePath") ?: return@forEach
            val xmlFile = module.layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile

            if (!xmlFile.exists()) {
                return@forEach
            }

            val moduleName = module.name
            val parser = groovy.xml.XmlParser()
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

            val xml = parser.parse(xmlFile)
            val counters = (xml as groovy.util.Node).get("counter") as List<*>

            var instruction = ""
            var branch = ""
            var line = ""
            var method = ""
            var classPercent = ""

            counters.forEach { counter ->
                val node = counter as groovy.util.Node
                val type = node.attribute("type")
                val missed = (node.attribute("missed") as String).toInt()
                val covered = (node.attribute("covered") as String).toInt()
                val total = missed + covered
                val percentage = if (total > 0) String.format("%.1f%%", covered * 100.0 / total) else "0.0%"

                when (type) {
                    "INSTRUCTION" -> instruction = percentage
                    "BRANCH" -> branch = percentage
                    "LINE" -> line = percentage
                    "METHOD" -> method = percentage
                    "CLASS" -> classPercent = percentage
                }
            }

            println(String.format("%-25s %12s %12s %12s %12s %12s",
                moduleName, instruction, branch, line, method, classPercent))
        }

        println("=".repeat(100) + "\n")
    }
}

subprojects {
    apply(plugin = "io.freefair.lombok")

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    plugins.withId("java") {
        apply(from = "${rootProject.projectDir}/gradle/jacoco.gradle.kts")

        tasks.named("test") {
            finalizedBy(rootProject.tasks.named("printCoverageSummary"))
        }
    }

}


configureByLabels("java") {
    apply(plugin = "org.gradle.java")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "com.coditory.integration-test")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.springBoot}")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.springCloud}")
            mavenBom("com.google.guava:guava-bom:${Versions.guava}")
        }

        dependencies {
            dependency("org.apache.commons:commons-lang3:${Versions.apacheCommonsLang}")
            dependency("org.apache.commons:commons-collections4:${Versions.apacheCommonsCollections}")
            dependency("com.navercorp.fixturemonkey:fixture-monkey-starter:${Versions.fixtureMonkey}")
            dependency("org.mapstruct:mapstruct:${Versions.mapstruct}")
            dependency("org.mapstruct:mapstruct-processor:${Versions.mapstruct}")
            dependency("com.fasterxml.jackson.core:jackson-databind:${Versions.jacksonCore}")

            dependency("org.junit.jupiter:junit-jupiter-api:5.10.2")
            dependency("org.junit.jupiter:junit-jupiter-params:5.10.2")
            dependency("org.junit.jupiter:junit-jupiter-engine:5.10.2")
            dependency("org.assertj:assertj-core:${Versions.assertjCore}")
            dependency("org.mockito:mockito-junit-jupiter:${Versions.mockitoCore}")

            dependency("com.epages:restdocs-api-spec:${Versions.restdocsApiSpec}")
            dependency("com.epages:restdocs-api-spec-mockmvc:${Versions.restdocsApiSpec}")
            dependency("com.epages:restdocs-api-spec-restassured:${Versions.restdocsApiSpec}")


            dependencySet("io.jsonwebtoken:${Versions.jwt}") {
                entry("jjwt-api")
                entry("jjwt-impl")
                entry("jjwt-jackson")
            }
        }
    }

    dependencies {
        val implementation by configurations
        val annotationProcessor by configurations

        val testImplementation by configurations
        val testRuntimeOnly by configurations

        val integrationImplementation by configurations
        val integrationRuntimeOnly by configurations

        implementation("com.google.guava:guava")

        implementation("org.apache.commons:commons-lang3")
        implementation("org.apache.commons:commons-collections4")
        implementation("org.mapstruct:mapstruct")

        annotationProcessor("org.mapstruct:mapstruct-processor")

        testImplementation("org.assertj:assertj-core")
        testImplementation("org.mockito:mockito-core")
        testImplementation("org.mockito:mockito-junit-jupiter")
        testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter")
        testImplementation("com.h2database:h2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        testImplementation("org.springframework.boot:spring-boot-starter-test")

        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
    }
}


configureByLabels("boot") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<Jar>("jar") {
        enabled = false
    }

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = true
        archiveClassifier.set("boot")
    }
}

configureByLabels("library") {
    apply(plugin = "java-library")

    tasks.getByName<Jar>("jar") {
        enabled = true
    }
}

configureByLabels("asciidoctor") {
    apply(plugin = "org.asciidoctor.jvm.convert")

    tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
        sourceDir(file("src/docs"))
        outputs.dir(file("build/docs"))
        attributes(
            mapOf(
                "snippets" to file("build/generated-snippets")
            )
        )
    }
}

configureByLabels("restdocs") {
    apply(plugin = "com.epages.restdocs-api-spec")
}

