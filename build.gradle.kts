plugins {
    java
    application
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("com.diffplug.gradle.spotless") version "3.18.0"
    id("com.github.spotbugs") version "1.6.10"
    id("io.freefair.lombok") version "3.1.4"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sparkjava:spark-core:2.7.2")
    implementation("org.json:json:20180813")
    implementation("com.auth0:java-jwt:3.7.0")
    implementation("software.amazon.awssdk:secretsmanager:2.4.12")
    implementation("software.amazon.awssdk:dynamodb:2.4.12")
    implementation("software.amazon.awssdk:kms:2.4.12")
    implementation("com.google.guava:guava:27.0.1-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("org.junit.jupiter:junit-jupiter:5.4.0")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.4.0")
    runtimeOnly("org.slf4j:slf4j-simple:1.7.26")
}

dependencyManagement {
  imports {
      mavenBom("software.amazon.awssdk:bom:2.4.12")
  }
}

buildscript {
  repositories {
      mavenCentral()
  }
  dependencies {
      classpath("io.spring.gradle:dependency-management-plugin:1.0.6.RELEASE")
  }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

spotless {
    java {
        googleJavaFormat()
    }
}

tasks.withType<com.github.spotbugs.SpotBugsTask> {
    reports {
        xml.isEnabled = false
        html.isEnabled = true
    }
}

application {
    mainClassName = "sys.esm.Api"
}


