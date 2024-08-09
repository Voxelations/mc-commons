import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("jvm-conventions")
    id("publishing-conventions")
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // Core API
    implementation(gradleApi())
    implementation(localGroovy())

    // Utils
    implementation(libs.lombok.plugin)
    implementation(libs.spotless.plugin.gradle)
    implementation(libs.shadow.plugin)

    // Paper env
    implementation(libs.plugin.yml)
    implementation(libs.paperweight.plugin)
}

tasks {
    withType<ShadowJar> {
        isZip64 = true
    }
}

gradlePlugin {
    plugins {
        create("Voxelations JVM Conventions") {
            id = "com.voxelations.common.gradle.jvm-conventions"
            implementationClass = "com.voxelations.common.gradle.JVMConventionsPlugin"
        }

        create("Voxelations Publishing Conventions") {
            id = "com.voxelations.common.gradle.publishing-conventions"
            implementationClass = "com.voxelations.common.gradle.PublishingConventionsPlugin"
        }

        create("Voxelations Paper Conventions") {
            id = "com.voxelations.common.gradle.paper-conventions"
            implementationClass = "com.voxelations.common.gradle.PaperConventionsPlugin"
        }
    }
}