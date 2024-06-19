rootProject.name = "WeatherPrediction"
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
include(":PredicationApp")
include(":Authentication")
include("rest-dto-common")
