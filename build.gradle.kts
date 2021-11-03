plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.dcendents.android-maven") version "2.1"
}

group = "com.github.xuxiangjun"
version = "1.2.0"

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}
