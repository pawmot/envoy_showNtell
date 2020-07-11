import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val node_version: String by project

plugins {
    application
    kotlin("jvm")
    id("com.google.cloud.tools.jib") version "2.4.0"
    id("nl.eleven.node-gradle.node") version "1.0.0"
}

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}

node {
    version = node_version
    download = false
    nodeModulesDir = file("${project.projectDir}/ng-src")
}

jib {
    from {
        image = "openjdk:14.0.1"
    }
    to {
        image = "pawmot/envoy-snt-fe"
        tags = setOf("$version")
    }
}

tasks {
    named<Delete>("clean") {
        delete("resource/static")
    }

    register<NpmTask>(name = "buildFrontend") {
        dependsOn("npmInstall")
        group = "build"
        description = "Build frontend side assets"
        inputs.dir("ng-src")
        outputs.dir("ng-src/dist")
        setArgs(listOf("run", "build"))
    }

    register<Copy>(name = "copyFrontendDist") {
        dependsOn("buildFrontend")
        group = "distribution"
        description = "Copy built frontend to resources"
        inputs.dir("ng-src/dist")
        outputs.dir("resources/static")
        from("ng-src/dist")
        into("resources/static")
    }

    named<ProcessResources>("processResources") {
        dependsOn("copyFrontendDist")
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
