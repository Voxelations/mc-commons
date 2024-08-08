rootProject.name = "mc-commons"

dependencyResolutionManagement {
    includeBuild("build-logic")
}

include("common")
include("paper")
include("gradle-plugin")