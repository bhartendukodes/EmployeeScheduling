rootProject.name = "employeeShiftScheduling"


pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion("1.9.22")
                "io.quarkus" -> useVersion("3.8.2")
            }
        }
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}