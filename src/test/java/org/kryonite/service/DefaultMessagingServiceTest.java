package org.kryonite.service;

import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kryonite.service.message.Message;
import org.kryonite.service.mock.MockActiveMqConnectionFactory;
import org.kryonite.service.model.Animal;
import org.kryonite.service.model.Person;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultMessagingServiceTest {

  private DefaultMessagingService testee;

  @BeforeEach
  void setUp() throws IOException, TimeoutException {
    testee = new DefaultMessagingService(new MockActiveMqConnectionFactory());
  }

  @Test
  void shouldSendAndReceiveMessage() throws IOException, InterruptedException {
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
    Thread.sleep(1_000);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(person, receivedMessages.get(0).getBody());
  }

  @Test
  void shouldSendAndReceiveMessageWithGivenRoutingKey() throws IOException, InterruptedException {
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
    Thread.sleep(1_000);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(person, receivedMessages.get(0).getBody());
  }

  @Test
  void shouldNotThrowErrorWhenQueueDoesSendAnotherBody() throws IOException, InterruptedException {
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
    Thread.sleep(1_000);

    // Assert
    assertEquals(2, receivedMessages.size());
  }
}
