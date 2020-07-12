group = "org.example"
version = "1.0-SNAPSHOT"

tasks.withType(Wrapper::class) {
    gradleVersion = "6.5.1"
}

plugins {
    kotlin("jvm") version "1.3.70" apply false
}

subprojects {
    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    }

    group = "com.pawmot.snt.envoy"
    version = "0.6.0"
}
