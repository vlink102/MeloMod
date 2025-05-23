@file:Suppress("UnstableApiUsage", "PropertyName")

import org.polyfrost.gradle.util.noServerRunConfigs
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import org.polyfrost.gradle.util.RelocationTransform.Companion.registerRelocationAttribute

// Adds support for kotlin, and adds the Polyfrost Gradle Toolkit
// which we use to prepare the environment.
plugins {
    idea
    java
    kotlin("jvm")
    id("org.polyfrost.multi-version")
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("io.freefair.lombok") version "6.6.1"
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.2"
    id("signing")
}

// Gets the mod name, version and id from the `gradle.properties` file.
val mod_name: String by project
val mod_version: String by project
val mod_id: String by project
val mod_archives_name: String by project
val mod_endpoint: String by project

// Replaces the variables in `ExampleMod.java` to the ones specified in `gradle.properties`.
blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
    replaceToken("@ENDPOINT@", mod_endpoint)
}

// Sets the mod version to the one specified in `gradle.properties`. Make sure to change this following semver!
version = mod_version
// Sets the group, make sure to change this to your own. It can be a website you own backwards or your GitHub username.
// e.g. com.github.<your username> or com.<your domain>
group = "me.vlink102"

// Sets the name of the output jar (the one you put in your mods folder and send to other people)
// It outputs all versions of the mod into the `versions/{mcVersion}/build` directory.
base {
    archivesName.set("$mod_archives_name-$platform")
}

// Configures Polyfrost Loom, our plugin fork to easily set up the programming environment.
loom {
    // Removes the server configs from IntelliJ IDEA, leaving only client runs.
    noServerRunConfigs()

    // Adds the tweak class if we are building legacy version of forge as per the documentation (https://docs.polyfrost.org)
    if (project.platform.isLegacyForge) {
        runConfigs {
            "client" {
                programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
                property("mixin.debug.export", "true") // Outputs all mixin changes to `versions/{mcVersion}/run/.mixin.out/class`
            }
        }
    }
    // Configures the mixins if we are building for forge
    if (project.platform.isForge) {
        forge {
            mixinConfig("mixins.${mod_id}.json")
        }
    }
    // Configures the name of the mixin "refmap"
    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")
}

// Creates the shade/shadow configuration, so we can include libraries inside our mod, rather than having to add them separately.
val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}
val modShade: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

// Configures the output directory for when building from the `src/resources` directory.
sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

// Adds the Polyfrost maven repository so that we can get the libraries necessary to develop the mod.
repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    //maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://jitpack.io")

}

val devenvMod: Configuration by configurations.creating {
    isTransitive = false
    isVisible = false
}
// Configures the libraries/dependencies for your mod.
dependencies {
    modCompileOnly("cc.polyfrost:oneconfig-$platform:0.2.2-alpha+")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    //implementation("org.asynchttpclient:async-http-client:3.0.0.Beta3")
    //implementation("net.hypixel:HypixelAPI:3.0.0")
    //compileOnly("net.hypixel:hypixel-api-transport-reactor:4.1")
    // Adds the OneConfig library, so we can develop with it.
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
    // Adds DevAuth, which we can use to log in to Minecraft in development.
    implementation("net.hypixel:mod-api:0.3.1")
    modRuntimeOnly("me.djtheredstoner:DevAuth-${if (platform.isFabric) "fabric" else if (platform.isLegacyForge) "forge-legacy" else "forge-latest"}:1.2.0")
    implementation("com.github.ILikePlayingGames:DiscordIPC:-SNAPSHOT")
    shade("it.unimi.dsi:fastutil:8.5.14")
    //shade("com.github.oshi:oshi-core:6.4.0")

    // If we are building for legacy forge, includes the launch wrapper with `shade` as we configured earlier, as well as mixin 0.7.11
    if (platform.isLegacyForge) {
        compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
        shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
    }
}

tasks {
    // Processes the `src/resources/mcmod.info`, `fabric.mod.json`, or `mixins.${mod_id}.json` and replaces
    // the mod id, name and version with the ones in `gradle.properties`
    processResources {
        inputs.property("id", mod_id)
        inputs.property("name", mod_name)
        val java = if (project.platform.mcMinor >= 18) {
            17 // If we are playing on version 1.18, set the java version to 17
        } else {
            // Else if we are playing on version 1.17, use java 16.
            if (project.platform.mcMinor == 17)
                16
            else
                8 // For all previous versions, we **need** java 8 (for Forge support).
        }
        val compatLevel = "JAVA_${java}"
        inputs.property("java", java)
        inputs.property("java_level", compatLevel)
        inputs.property("version", mod_version)
        inputs.property("mcVersionStr", project.platform.mcVersionStr)
        filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json", "mods.toml")) {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr
                )
            )
        }
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr.substringBeforeLast(".") + ".x"
                )
            )
        }

    }

    // Configures the resources to include if we are building for forge or fabric.
    withType(Jar::class.java) {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml")
            } else {
                exclude("mcmod.info")
            }
        }
    }

    // Configures our shadow/shade configuration, so we can
    // include some dependencies within our mod jar file.
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade, modShade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }


    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    jar {
        // Sets the jar manifest attributes.
        if (platform.isLegacyForge) {
            manifest.attributes += mapOf(
                "ModSide" to "CLIENT", // We aren't developing a server-side mod
                "ForceLoadAsMod" to true, // We want to load this jar as a mod, so we force Forge to do so.
                "TweakOrder" to "0", // Makes sure that the OneConfig launch wrapper is loaded as soon as possible.
                "MixinConfigs" to "mixins.${mod_id}.json", // We want to use our mixin configuration, so we specify it here.
                "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker" // Loads the OneConfig launch wrapper.
            )
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
}