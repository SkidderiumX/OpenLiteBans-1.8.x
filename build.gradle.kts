plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    // Spigot 1.8.9 runs on Java 8; the API artifact published for the 1.8.x line is 1.8.8-R0.1.
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    // ProtocolLib 3.6.5 is the last line that supports the legacy 1.8 packet API used here.
    compileOnly("com.comphenix.protocol:ProtocolLib:3.6.5")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("org.slf4j:slf4j-api:2.0.17")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly(files("libs/ServerListPlus-3.5.0-Universal.jar"))
}

java {
    // Build with a modern JDK but emit Java 8 bytecode so the jar runs on 1.8.9's JVM.
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

// This build targets the Spigot/Bukkit jar only. Fabric/mixin and the BungeeCord/Velocity
// proxy implementations need newer Java and Minecraft APIs, so they are not part of the 1.8.9 jar.
sourceSets.main {
    java {
        exclude(
            "litebans/BungeePlugin.java",
            "litebans/VelocityPlugin.java",
            "litebans/FabricPlugin.java",
            "litebans/FabricMixinCheckIn.java",
            "litebans/mixin/**",
            "litebans/platform/bungee/**",
            "litebans/platform/velocity/**",
            "litebans/platform/fabric/**",
            "litebans/chat/AdventureComponentParser.java",
            "litebans/platform/bukkit/AuthMeLoginListener.java",
            "litebans/command/CommandPermissionMapper.java",
            "litebans/command/MappedCommand.java",
        )
    }
}

tasks {
    register("printCompileClasspath") {
        doLast { println(sourceSets.main.get().compileClasspath.asPath) }
    }
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    withType<JavaCompile>().configureEach {
        options.release.set(8)
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
    }
}
