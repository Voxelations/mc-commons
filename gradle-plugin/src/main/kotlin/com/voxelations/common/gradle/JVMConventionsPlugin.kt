package com.voxelations.common.gradle

import com.diffplug.gradle.spotless.SpotlessPlugin
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class JVMConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply<JavaLibraryPlugin>()
            apply<SpotlessPlugin>()
            apply<ShadowPlugin>()
            apply<LombokPlugin>()

            group = "com.voxelations"

            repositories {
                mavenCentral()
                maven("https://oss.sonatype.org/content/repositories/snapshots/")
                maven("https://repo.papermc.io/repository/maven-public/")
                maven("https://maven.voxelations.com/public/")
            }

            configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(21))
                withJavadocJar()
                withSourcesJar()
            }

            tasks.apply {
                named("build") {
                    dependsOn("shadowJar")
                }

                withType<ShadowJar> {
                    archiveFileName.set("${rootProject.name}-${project.name}-${project.version}.jar")
                }

                withType<JavaCompile> {
                    options.encoding = Charsets.UTF_8.name()
                    options.release.set(21)
                    options.compilerArgs.add("-parameters")
                }

                withType<ProcessResources> {
                    filteringCharset = Charsets.UTF_8.name()
                }

                withType<Javadoc> {
                    options.encoding = Charsets.UTF_8.name()
                }
            }
        }
    }
}