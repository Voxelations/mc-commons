import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("publishing-conventions")
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.plugin.yml.paper)
}

dependencies {
    // Common
    annotationProcessor(project(":common"))
    api(project(":common"))

    // Paper env
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnlyApi(libs.paper.api)

    // Libraries
    api(libs.cloud.paper)
    api(libs.smart.invs)
}

configure<PaperPluginDescription> {
    name = "mc-commons"
    version = rootProject.version.toString()
    apiVersion = "1.19"
    bootstrapper = "com.voxelations.common.platform.paper.internal.CommonBootstrapper"
    main = "com.voxelations.common.platform.paper.internal.CommonPlugin"

    author = "Voxelations"
    website = "https://voxelations.com/"
}