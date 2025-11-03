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

fun parseCoverage(xmlFile: File): Map<String, String> {
    if (!xmlFile.exists()) {
        return emptyMap()
    }

    val parser = groovy.xml.XmlParser()
    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
    parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

    val xml = parser.parse(xmlFile)
    val counters = (xml as groovy.util.Node).get("counter") as List<*>

    val coverage = mutableMapOf<String, String>()

    counters.forEach { counter ->
        val node = counter as groovy.util.Node
        val type = node.attribute("type") as String
        val missed = (node.attribute("missed") as String).toInt()
        val covered = (node.attribute("covered") as String).toInt()
        val total = missed + covered
        val percentage = if (total > 0) String.format("%.1f%%", covered * 100.0 / total) else "0.0%"

        coverage[type] = percentage
    }

    return coverage
}

/*콘솔 출력*/
tasks.register("printCoverageSummary") {
    group = "verification"
    description = "Print module-by-module coverage summary to console"

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
            val coverage = parseCoverage(xmlFile)

            if (coverage.isEmpty()) {
                return@forEach
            }

            println(String.format("%-25s %12s %12s %12s %12s %12s",
                module.name,
                coverage["INSTRUCTION"] ?: "N/A",
                coverage["BRANCH"] ?: "N/A",
                coverage["LINE"] ?: "N/A",
                coverage["METHOD"] ?: "N/A",
                coverage["CLASS"] ?: "N/A"))
        }

        println("=".repeat(100) + "\n")
    }
}

/*md 문서 생성*/
tasks.register("printCoverageSummaryMarkdown") {
    group = "verification"
    description = "Generate module-by-module coverage summary in Markdown format to file"

    val outputFile = layout.buildDirectory.file("reports/coverage-summary.md")

    outputs.file(outputFile)

    doLast {
        val content = buildString {
            appendLine("## Code Coverage Report (모듈 별)")
            appendLine()
            appendLine("| Module | Instruction | Branch | Line | Method | Class |")
            appendLine("|--------|-------------|--------|------|--------|-------|")

            modules.forEach { modulePath ->
                val module = project.findProject(":$modulePath") ?: return@forEach
                val xmlFile = module.layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
                val coverage = parseCoverage(xmlFile)

                if (coverage.isEmpty()) {
                    return@forEach
                }

                appendLine("| ${module.name} | ${coverage["INSTRUCTION"] ?: "N/A"} | ${coverage["BRANCH"] ?: "N/A"} | ${coverage["LINE"] ?: "N/A"} | ${coverage["METHOD"] ?: "N/A"} | ${coverage["CLASS"] ?: "N/A"} |")
            }

            appendLine()
            appendLine("Generated: ${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
        }

        outputFile.get().asFile.apply {
            parentFile.mkdirs()
            writeText(content)
        }

        println(content)
    }
}
