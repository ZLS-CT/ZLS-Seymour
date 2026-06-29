pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
}

includeBuild("../essential-gradle-toolkit")

rootProject.name = "ZLS-Seymour"
rootProject.buildFileName = "root.gradle.kts"

val versionList = listOf(
    "26.2-fabric",
    "26.1.2-fabric",
)
versionList.forEach { version ->
    file("versions/$version").mkdirs()
}

versionList.forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}
