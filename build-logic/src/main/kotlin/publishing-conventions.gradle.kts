plugins {
    id("jvm-conventions")
    `maven-publish`
}

publishing {
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
