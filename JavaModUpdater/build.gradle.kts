plugins {
    java
}

tasks.named<Jar>("jar") {
    archiveFileName.set("JavaModUpdater.jar")
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "com.zephy.zls.javamodupdater.postexit.PostExitMain"
            )
        )
    }
}
