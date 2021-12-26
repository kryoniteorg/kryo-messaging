plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.sonarqube") version "3.3"
    jacoco
    checkstyle
}

group = "org.kryonite"
version = "1.3.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.rabbitmq:amqp-client:5.14.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.13.1")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")


    testImplementation("com.github.fridujo:rabbitmq-mock:1.1.1")
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-junit-jupiter:4.2.0")
    testImplementation("org.awaitility:awaitility:4.1.1")

    testCompileOnly("org.projectlombok:lombok:1.18.20")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks.test {
    finalizedBy("jacocoTestReport")
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
}

checkstyle {
    toolVersion = "9.0"
    config = project.resources.text.fromUri("https://kryonite.org/checkstyle.xml")
}

sonarqube {
    properties {
        property("sonar.projectKey", "kryonitelabs_kryo-messaging")
        property("sonar.organization", "kryonitelabs")
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
