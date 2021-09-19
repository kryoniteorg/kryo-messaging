package org.kryonite.kryomessaging.service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kryonite.kryomessaging.service.DefaultMessagingService.DEFAULT_RETRY_COUNT;

import com.rabbitmq.client.BuiltinExchangeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;
import org.kryonite.kryomessaging.service.mock.MockActiveMqConnectionFactory;
import org.kryonite.kryomessaging.service.model.Animal;
import org.kryonite.kryomessaging.service.model.Person;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultMessagingServiceTest {

  private DefaultMessagingService testee;

  @BeforeEach
  void setUp() throws IOException, TimeoutException {
    testee = new DefaultMessagingService(new MockActiveMqConnectionFactory());
  }

  @Test
  void shouldSendAndReceiveMessage() throws IOException {
    // Arrange
    String queue = "queue";
    String exchange = "exchange";
    Person person = new Person("Tom", UUID.randomUUID(), 21, 12345.67);

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue, exchange);

    List<Message<Person>> receivedMessages = new ArrayList<>();
    testee.startConsuming(queue, receivedMessages::add, Person.class);

    // Act
    testee.sendMessage(Message.create(exchange, person, ""));
    await()
        .atMost(1, TimeUnit.SECONDS)
        .until(() -> receivedMessages.size() >= 1);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(person, receivedMessages.get(0).getBody());
  }

  @Test
  void shouldSendAndReceiveMessageWithGivenRoutingKey() throws IOException {
    // Arrange
    String queue1 = "queue1";
    String queue2 = "queue2";
    String exchange = "exchange";
    String routingKey1 = "person";
    String routingKey2 = "animal";

    Person person = new Person("Tom", UUID.randomUUID(), 21, 12345.67);
    Animal animal = new Animal(12);

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue1, exchange, routingKey1);
    testee.bindQueueToExchange(queue2, exchange, routingKey2);

    List<Message<Person>> receivedMessages = new ArrayList<>();
    testee.startConsuming(queue1, receivedMessages::add, Person.class);
    testee.startConsuming(queue2, message -> log.info("Received animal: " + message.getBody()), Animal.class);

    // Act
    testee.sendMessage(Message.create(exchange, person, routingKey1));
    testee.sendMessage(Message.create(exchange, animal, routingKey2));
    await()
        .atMost(1, TimeUnit.SECONDS)
        .until(() -> receivedMessages.size() >= 1);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(person, receivedMessages.get(0).getBody());
  }

  @Test
  void shouldNotThrowError_WhenQueueDoesSendAnotherBody() throws IOException {
    // Arrange
    String queue1 = "queue1";
    String queue2 = "queue2";
    String exchange = "exchange";
    String routingKey1 = "person";
    String routingKey2 = "animal";

    Person person = new Person("Tom", UUID.randomUUID(), 21, 12345.67);
    Animal animal = new Animal(12);

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue1, exchange, routingKey1);
    testee.bindQueueToExchange(queue2, exchange, routingKey2);

    List<Message<Person>> receivedMessages = new ArrayList<>();
    testee.startConsuming(queue1, receivedMessages::add, Person.class);

    // Act
    testee.sendMessage(Message.create(exchange, person, routingKey1));
    testee.sendMessage(Message.create(exchange, animal, routingKey1));
    await()
        .atMost(1, TimeUnit.SECONDS)
        .until(() -> receivedMessages.size() >= 2);

    // Assert
    assertEquals(2, receivedMessages.size());
  }

  @Test
  void shouldResendMessage_WhenExceptionIsThrownOnTheConsumer() throws IOException {
    // Arrange
    String queue = "queue";
    String exchange = "exchange";

    Person person = new Person("Tom", UUID.randomUUID(), 21, 12345.67);

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue, exchange);

    List<Message<Person>> failedMessages = new ArrayList<>();
    List<Message<Person>> receivedMessages = new ArrayList<>();
    testee.startConsuming(queue, new MessageCallback<>() {
      private int counter = 0;

      @Override
      public void messageReceived(Message<Person> message) {
        counter++;
        if (counter <= 1) {
          failedMessages.add(message);
          throw new IllegalArgumentException("Failed to consume message!");
        }

        receivedMessages.add(message);
      }
    }, Person.class);

    // Act
    testee.sendMessage(Message.create(exchange, person));
    await()
        .atMost(1, TimeUnit.SECONDS)
        .until(() -> receivedMessages.size() >= 1 && failedMessages.size() >= 1);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(1, failedMessages.size());
  }

  @Test
  void shouldDeleteMessageAfterRetries() throws IOException {
    // Arrange
    String queue = "queue";
    String exchange = "exchange";

    Person person = new Person("Tom", UUID.randomUUID(), 21, 12345.67);

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue, exchange);

    List<Message<Person>> failedMessages = new ArrayList<>();
    testee.startConsuming(queue, message -> {
      failedMessages.add(message);
      throw new IllegalArgumentException("Failed to consume message!");
    }, Person.class);

    // Act
    testee.sendMessage(Message.create(exchange, person));
    await()
        .atMost(1, TimeUnit.SECONDS)
        .until(() -> failedMessages.size() >= DEFAULT_RETRY_COUNT);

    // Assert
    assertEquals(DEFAULT_RETRY_COUNT, failedMessages.size());
  }
}
