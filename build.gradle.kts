plugins {
    kotlin("jvm")
    id("maven-publish")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

group = "com.zephy.zlsseymour"
version = "4.3.0"

tasks {
    processResources {
        val version = project.version
        val minFabricApiVersion = project.findProperty("min-fabric-api")?.toString()
        val javaVersion = project.java.toolchain.languageVersion.get().asInt()

        inputs.property("version", version)
        inputs.property("min_fabric_api_version", minFabricApiVersion.toString())
        inputs.property("compatibilityLevel", javaVersion)

        filesMatching("fabric.mod.json") {
            expand(mapOf(
                "version" to version,
                "min_fabric_api_version" to minFabricApiVersion,
            ))
        }
    }
}

afterEvaluate {
    val hasRemapJar = tasks.findByName("remapJar") != null
    val outputTaskName = if (hasRemapJar) "remapJar" else "jar"

    tasks.register<Copy>("collectJars") {
        group = "build"
        description = "Copies this version's non-shadowed JARs to main/jars"

        val outputDir = projectDir.resolve("../../jars").normalize()
        dependsOn(outputTaskName)

        from(tasks.named(outputTaskName)) {
            include("*.jar")
            exclude { it.name.contains(" 1.2") && it.name.contains("-all") }
            rename {
                "${rootProject.name}-${version}+${project.platform.mcVersionStr}.jar"
            }
        }
        into(outputDir)
    }

    tasks.named("build") {
        finalizedBy("collectJars")
    }

    configurations.named("default") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }

    artifacts {
        add("default", tasks.named(outputTaskName))
    }
}
