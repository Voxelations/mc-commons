package com.voxelations.common.gradle

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import net.minecrell.pluginyml.paper.PaperPlugin
import net.minecrell.pluginyml.paper.PaperPluginDescription
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import java.io.File
import java.util.stream.Collectors

class PaperConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply<PaperweightUser>()
            apply<PaperPlugin>()

            dependencies {
                the<PaperweightUserDependenciesExtension>().paperDevBundle(findProperty("minecraft-version") as? String ?: "1.21-R0.1-SNAPSHOT")
            }

            configure<PaperPluginDescription> {
                name = project.rootProject.name
                version = project.rootProject.version.toString()
                apiVersion = "1.19"

                author = "Voxelations"
                website = "https://voxelations.com/"

                serverDependencies.register("mc-commons") {
                    load = PaperPluginDescription.RelativeLoadOrder.BEFORE
                    joinClasspath = true
                }

                main = findClass { it.contains(" extends PaperPlugin") }
            }
        }
    }

    /**
     * Finds a class that matches the given filter.
     *
     * @param textFilter The filter to match the class with
     * @return The fully qualified loader class' name or null
     */
    private fun Project.findClass(textFilter: (String) -> Boolean): String? {
        val sourceSet = the<SourceSetContainer>().findByName("main") ?: run {
            project.logger.warn("No main source set found")
            return null
        }

        val classesDir = sourceSet.allSource.srcDirs.first()
        val candidates = sourceSet.allSource.srcDirs.flatMap { it.walkTopDown() }
            .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
            .filter { textFilter(it.readText(Charsets.UTF_8)) }
            .map { it ->
                // Get the class location in plugin.yml format
                val file = it.relativeTo(classesDir)
                val components = file.path
                    .replace(File.separatorChar, '.')
                    .replace("java", "")
                    .split('.')

                components.stream().filter { it.isNotEmpty() }.collect(Collectors.joining("."))
            }
            .toList()

        return when {
            candidates.size > 1 -> {
                val first = candidates.first()
                project.logger.warn("Multiple candidates found, auto selecting $first")
                first
            }
            candidates.size == 1 -> candidates.first().also { project.logger.warn("Found candidate: $it") }
            else -> null
        }
    }
}