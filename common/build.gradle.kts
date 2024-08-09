plugins {
    id("jvm-conventions")
    id("shadow-conventions")
    id("publishing-conventions")
}

dependencies {
    // Lombok
    api(libs.lombok)
    annotationProcessor(libs.lombok)
    // Logging (for lombok)
    compileOnlyApi(libs.slf4j.api)

    // DI
    api(libs.guice)

    // Configs
    api(libs.configurate.yaml)

    // Data
    api(libs.jooq)
    api(libs.hikaricp)
    api(libs.mysql.connector.j)
    api(libs.postgresql)

    // Adventure
    compileOnlyApi(libs.adventure.api)
    compileOnlyApi(libs.adventure.text.minimessage)

    // Cloud commands
    api(libs.cloud.core)
    api(libs.cloud.annotations)
}