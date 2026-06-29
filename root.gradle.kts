plugins {
    kotlin("jvm") version "2.3.10" apply false // Don't bump, depends on preprocessor
    id("gg.essential.multi-version.root")
}

preprocess.strictExtraMappings.set(true)
preprocess {
    val fabric26_02_00 = createNode("26.2-fabric", 26_02_00, "official")
    val fabric26_01_02 = createNode("26.1.2-fabric", 26_01_02, "official")

    fabric26_02_00.link(fabric26_01_02)
}
