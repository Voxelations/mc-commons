import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    named("build") {
        dependsOn("shadowJar")
    }

    withType<ShadowJar> {
        archiveFileName.set("${rootProject.name}-${project.name}-${project.version}.jar")
    }
}
