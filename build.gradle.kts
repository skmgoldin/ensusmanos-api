plugins {
    java
    application
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
    id("com.diffplug.gradle.spotless") version "3.18.0"
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
    implementation("com.google.guava:guava:27.0.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
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

application {
    mainClassName = "sys.JoNet.core.Core"
}


