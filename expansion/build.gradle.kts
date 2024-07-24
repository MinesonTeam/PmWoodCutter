plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("io.github.goooler.shadow") version "8.1.7"
}

val spigot = property("spigot") as String
val projectVersion: String by project

dependencies {
    compileOnly("org.spigotmc:spigot-api:$spigot")
}

tasks {
    runServer.get().minecraftVersion("1.20.3")
    compileJava.get().options.encoding = Charsets.UTF_8.name()
    javadoc.get().options.encoding = Charsets.UTF_8.name()
    shadowJar.get().archiveFileName.set("Expansion-pmwoodcutter.jar")
    compileJava.get().dependsOn(clean)
    build.get().dependsOn(shadowJar)
}