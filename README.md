# kryo-messaging
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-messaging&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kryoniteorg_kryo-messaging)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-messaging&metric=coverage)](https://sonarcloud.io/summary/new_code?id=kryoniteorg_kryo-messaging)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-messaging&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=kryoniteorg_kryo-messaging)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-messaging&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=kryoniteorg_kryo-messaging)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=kryoniteorg_kryo-messaging&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=kryoniteorg_kryo-messaging)

This library contains a simple `MessagingService` which simplifies the setup and work
with RabbitMQ and the AMQP protocol.

## Usage
### Gradle
```java
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.kryoniteorg:kryo-messaging:2.0.1")
}
```

### Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.kryoniteorg</groupId>
  <artifactId>kryo-messaging</artifactId>
  <version>2.0.1</version>
</dependency>
```
## Examples
Examples can be found in the tests [here](src/test/java/org/kryonite/kryomessaging/service/DefaultMessagingServiceTest.java) and the latest
javadoc is published to:
https://kryoniteorg.github.io/kryo-messaging/latest/
