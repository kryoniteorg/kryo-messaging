plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.kryonite"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.rabbitmq:amqp-client:5.13.1")
    api("com.fasterxml.jackson.core:jackson-databind:2.12.5")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    testImplementation("com.github.fridujo:rabbitmq-mock:1.1.1")
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testImplementation("org.mockito:mockito-junit-jupiter:3.12.4")

    testCompileOnly("org.projectlombok:lombok:1.18.20")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
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
