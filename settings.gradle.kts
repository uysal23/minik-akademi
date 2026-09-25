pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MinikAkademi"

include(":app")

include(":core:model")
include(":core:navigation")
include(":core:design-system")
include(":core:datastore")

include(":feature:splash")
include(":feature:onboarding")
include(":feature:child-profile")
include(":feature:avatar-selection")
include(":feature:child-home")
include(":feature:learning-path")
include(":feature:tracing")
include(":feature:literacy")
include(":feature:parent-gate")
include(":feature:parent-dashboard")
include(":feature:settings")
