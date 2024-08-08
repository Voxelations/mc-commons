package com.voxelations.common.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven

class PublishingConventionsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply<MavenPublishPlugin>()

            configure<PublishingExtension> {
                repositories {
                    maven("https://maven.voxelations.com/public/") {
                        credentials {
                            username = System.getProperty("VOXELATIONS_MAVEN_PUBLIC_USERNAME")
                            password = System.getProperty("VOXELATIONS_MAVEN_PUBLIC_PASSWORD")
                        }
                        authentication {
                            create<BasicAuthentication>("basic")
                        }
                    }
                }

                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])

                        artifactId = "${rootProject.name}-${project.name}"
                    }
                }
            }
        }
    }
}