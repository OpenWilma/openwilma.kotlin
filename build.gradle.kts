import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("groovy")
    id("java-library")
    id("maven-publish")
    kotlin("jvm") version "1.6.21"
    id("org.hibernate.build.maven-repo-auth") version "3.0.3"
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            groupId = "org.openwilma"
            artifactId = "kotlin"
            version = "0.9.18-BETA"
        }
    }
    repositories {
        maven {
            url = uri("https://maven.testausserveri.fi/openwilma")
            name = "Testausmaven"
        }
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20220924")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.helger:ph-css:6.5.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
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