import net.darkhax.curseforgegradle.TaskPublishCurseForge

tasks.withType<TaskPublishCurseForge>().configureEach {
    uploadArtifacts.forEach { it.addEnvironment("Client", "Server") }
}

// Keep the server free of physical-client renderer classes.
tasks.withType<JavaExec>().configureEach {
    if (name.startsWith("runServer")) {
        doFirst {
            classpath = classpath.filter { file ->
                listOf("Angelica", "ModularUI", "Baubles-Expanded").none {
                    file.name.contains(it, ignoreCase = true)
                }
            }
        }
    }
    if (name.startsWith("runClient") || name.startsWith("runServer")) {
        notCompatibleWithConfigurationCache("Development launches filter and snapshot the resolved classpath.")
        jvmArgs("-Dmixin.debug.export=true")
        doFirst {
            val snapshot = java.nio.file.Files.createTempDirectory("hohenangst-$name-").toFile()
            val buildRoot = layout.buildDirectory.get().asFile.toPath().toAbsolutePath().normalize()
            classpath = files(classpath.files.mapIndexed { index, entry ->
                if (entry.toPath().toAbsolutePath().normalize().startsWith(buildRoot)) {
                    val destination = File(snapshot, "$index/${entry.name}")
                    if (entry.isDirectory) entry.copyRecursively(destination)
                    else { destination.parentFile.mkdirs(); entry.copyTo(destination) }
                    destination
                } else entry
            })
        }
    }
}

if (providers.gradleProperty("heightAudit").isPresent()) {
    tasks.withType<JavaExec>().configureEach {
        if (name.startsWith("runServer")) jvmArgs("-Dhohenangst.heightAudit=true", "-Dfml.queryResult=confirm")
    }
}

if (providers.gradleProperty("heightClientAudit").isPresent()) {
    tasks.withType<JavaExec>().configureEach {
        if (name.startsWith("runClient")) jvmArgs("-Dfml.queryResult=confirm")
    }
}

if (providers.gradleProperty("sectionGallery").isPresent()) {
    tasks.withType<JavaExec>().configureEach {
        if (name.startsWith("runServer")) jvmArgs("-Dhohenangst.sectionGallery=true")
    }
}
