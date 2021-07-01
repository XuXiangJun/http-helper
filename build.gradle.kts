plugins {
    kotlin("jvm") version "1.5.20"
    id("com.github.dcendents.android-maven") version "2.1"
}

group = "com.github.xuxiangjun"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib"))
}
