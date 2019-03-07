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
    //testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
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

tasks.register<Exec>("dTestNetwork") {
  commandLine("docker", "network", "create", "--driver", "bridge",
  System.getenv("JONET_API_TEST_NET_NAME"))
}

tasks.register<Exec>("dbuild") {
  commandLine("docker", "build", "-t", System.getenv("JONET_API_IMAGE_REGISTRY"), ".")
}

tasks.register("ddynamo") {
  dependsOn("dTestNetwork")
  val portMapping = StringBuilder().append(System.getenv("JONET_API_TEST_USERS_DB_PORT")).append(":8000")
    .toString()
  doLast {
    ProcessBuilder().command("docker", "run", "--name", System.getenv("JONET_API_TEST_USERS_DB_HOST_NAME"), "-p",
    portMapping, "--network", System.getenv("JONET_API_TEST_NET_NAME"),
    "amazon/dynamodb-local").start()
  }
}

tasks.register<Exec>("dtest") {
  dependsOn("dTestNetwork")
  dependsOn("dbuild")
  dependsOn("ddynamo")
  val jonetEnv = StringBuilder().append("JONET_API_ENV=").append(environment["JONET_API_ENV"])
  val jonetPort = StringBuilder().append("JONET_API_PORT=").append(environment["JONET_API_PORT"])
  val jonetTest = StringBuilder().append("JONET_API_TEST=").append(environment["JONET_API_TEST"])
  val awsRegion = StringBuilder().append("AWS_REGION=").append(environment["AWS_REGION"])
  val awsAccessKeyId = StringBuilder().append("AWS_ACCESS_KEY_ID=")
    .append(environment["AWS_ACCESS_KEY_ID"])
  val awsSecretAccessKey = StringBuilder().append("AWS_SECRET_ACCESS_KEY=")
    .append(environment["AWS_SECRET_ACCESS_KEY"])
  val jonetTestUsersDbPort = StringBuilder().append("JONET_API_TEST_USERS_DB_PORT=")
    .append(environment["JONET_API_TEST_USERS_DB_PORT"])
  val jonetTestUsersDbHostName = StringBuilder().append("JONET_API_TEST_USERS_DB_HOST_NAME=")
    .append(environment["JONET_API_TEST_USERS_DB_HOST_NAME"])
  val portMapping = StringBuilder().append(environment["JONET_API_PORT"]).append(":")
    .append(environment["JONET_API_PORT"]).append("/tcp")

  commandLine("docker", "run", "-e", jonetEnv, "-e", jonetPort, "-e", awsRegion, "-e",
  awsAccessKeyId, "-e", awsSecretAccessKey, "-e", jonetTestUsersDbPort, "-e", jonetTest,
  "-e", jonetTestUsersDbHostName, "-p", portMapping, "--network", System.getenv("JONET_API_TEST_NET_NAME"),
  System.getenv("JONET_API_IMAGE_REGISTRY"), "./gradlew", "build") 

  finalizedBy("dCleanup")
}

tasks.register<Exec>("dCleanup") {
  dependsOn("dKillTestDb")
  commandLine("docker", "system", "prune", "-f")
}
  
tasks.register<Exec>("dKillTestDb") {
  commandLine("docker", "kill", System.getenv("JONET_API_TEST_USERS_DB_HOST_NAME"))
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
    mainClassName = "sys.JoNet.Api"
}


