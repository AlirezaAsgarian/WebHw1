plugins {
    id("java")
}

group = "ir.aa"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok:1.18.28")
}