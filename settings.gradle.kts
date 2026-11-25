pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    // https://stonecutter.kikugie.dev/
    id("dev.kikugie.stonecutter") version "0.10"

    // Applies the right Loom variant for each version (remapping for <=1.21.11, plain for 26.1+)
    // https://codeberg.org/KikuGie/loom-back-compat
    id("dev.kikugie.loom-back-compat") version "0.4.3"

    // Lets Gradle download the JDKs each version needs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // One jar is built per version listed here. See stonecutter.properties.toml for the
        // range of Minecraft versions each jar supports.
        versions("1.14.4", "1.17.1", "1.20.4", "1.21.11", "26.3")
        vcsVersion = "26.3"
    }
}

rootProject.name = "RegSyncFix"
