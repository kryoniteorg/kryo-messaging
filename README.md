# Messaging library
This library contains a simple `MessagingService` which simplifies the setup and work
with RabbitMQ and the AMQP protocol.

## Usage
### Gradle
```java
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.kryonitelabs:messaging:1.2.0")
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
  <groupId>com.github.kryonitelabs</groupId>
  <artifactId>messaging</artifactId>
  <version>1.2.0</version>
</dependency>
```
## Examples
Examples can be found in the tests [here](src/test/java/org/kryonite/service/DefaultMessagingServiceTest.java) and the latest
javadoc is published to:
https://kryonitelabs.github.io/messaging/latest/
