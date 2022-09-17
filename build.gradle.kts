import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("groovy")
    id("java")
    kotlin("jvm") version "1.6.21"
}

group = "org.openwilma.kotlin"
version = "0.9-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("org.apache.groovy:groovy:4.0.5")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20220320")
    implementation("org.jsoup:jsoup:1.15.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}