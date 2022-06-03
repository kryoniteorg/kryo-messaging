plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.sonarqube") version "3.3"
    id("io.freefair.lombok") version "6.4.2"
    jacoco
    checkstyle
}

group = "org.kryonite"
version = "2.0.1"

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.8.2"

    api("com.rabbitmq:amqp-client:5.14.2")
    api("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")

    testImplementation("com.github.fridujo:rabbitmq-mock:1.1.1")
    testImplementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:4.6.1")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

tasks.test {
    finalizedBy("jacocoTestReport")
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

checkstyle {
    toolVersion = "9.2.1"
    config = project.resources.text.fromUri("https://kryonite.org/checkstyle.xml")
}

sonarqube {
    properties {
        property("sonar.projectKey", "kryoniteorg_kryo-messaging")
        property("sonar.organization", "kryoniteorg")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

publishing {
    publications {
        create<MavenPublication>("java") {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            from(components["java"])
        }
    }
}
