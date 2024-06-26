plugins {
    kotlin("jvm") version "1.9.22"
    id("maven-publish")
}

val kotlinVersion = "1.9.22"

val libGroup = "com.github.XuXiangJun"
val libVersion = "1.4.0"

group = libGroup
version = libVersion

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = libGroup
            artifactId = "http-helper"
            version = libVersion

            from(components["java"])
        }
    }
}
