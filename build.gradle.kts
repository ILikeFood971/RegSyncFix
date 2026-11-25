plugins {
    // Applies the correct Loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    id("com.modrinth.minotaur") version "2.+"
}

version = "${sc.properties["mod.version"]}+${sc.current.version}"
base.archivesName = sc.properties["mod.id"]

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://maven.bawnorton.com/releases", "Bawnorton", "com.github.bawnorton.mixinsquared")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    // Applies Mojang Mappings on obfuscated versions
    loomx.applyMojangMappings()

    // Use `mod{dependency type}` even on 26.1+ - loom-back-compat converts them
    modImplementation("net.fabricmc:fabric-loader:${sc.properties["deps.fabric_loader"]}")

    // Runtime only: the mixins name Fabric API classes as strings.
    // Otherwise, remapper would pin each mixin to the exact method signatures of one version.
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"]}") {
        // Old Fabric API versions depend on an old loader, which would end up on the classpath twice
        exclude(group = "net.fabricmc", module = "fabric-loader")
    }

    // Only bundled in the builds that cover 1.14 to early 1.17.
    val mixinSquared = "com.github.bawnorton.mixinsquared:mixinsquared-fabric:0.3.9"
    compileOnly(mixinSquared)
    if (sc.current.parsed < "1.18") {
        modRuntimeOnly(mixinSquared)
        include(mixinSquared)
    }
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run") // Shares the run directory between versions
        jvmArguments.add("-Dmixin.debug.export=true") // Exports transformed classes for debugging
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    processResources {
        val props = mapOf(
            "id" to sc.properties["mod.id"],
            "name" to sc.properties["mod.name"],
            "version" to sc.properties["mod.version"],
            "minecraft" to sc.properties["mod.mc_compat"],
            "loader" to sc.properties["mod.loader_compat"],
            "java" to "JAVA_${requiredJava.majorVersion}",
        )
        inputs.properties(props)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(props) }
    }

    // Includes the license file in the built mod
    withType<Jar> {
        val name = sc.properties["mod.id"]
        inputs.property("mod_id", name)
        from("../../LICENSE") { rename { "$it-$name" } }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds the mod jar and copies results to `build/libs/{mod version}/`"

        inputs.property("version", sc.properties["mod.version"])
        // loomx.mod(Sources)Jar returns the jar task for the applied loom variant
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${sc.properties["mod.version"]}"))
    }
}

modrinth {
    // Set the MODRINTH_TOKEN environment variable to publish. Run `./gradlew modrinth` to upload every version.
    token = providers.environmentVariable("MODRINTH_TOKEN")
    projectId = "regsyncfix"
    versionNumber = project.version.toString()
    versionName = "${sc.properties["mod.version"]} for ${sc.current.version}"
    versionType = "release"
    // Pulls the `## <mod.version>` section from CHANGELOG.md. Upload fails if a match cannot be found.
    val modVersion = sc.properties["mod.version"]
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText.map { text ->
        text.replace("\r\n", "\n")
            .substringAfter("\n## $modVersion\n", "")
            .substringBefore("\n## ")
            .trim()
            .ifEmpty { error("CHANGELOG.md has no '## $modVersion' section") }
    }
    uploadFile.set(loomx.modJar)
    gameVersions.addAll(sc.properties.rawOrNull("mod", "mc_releases")?.asList().orEmpty().map { it.toString() })
    loaders.add("fabric")
    dependencies {
        required.project("fabric-api")
    }
}
