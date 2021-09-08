package org.kryonite.service;

import com.rabbitmq.client.BuiltinExchangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    String message = "message";

    testee.setupExchange(exchange, BuiltinExchangeType.DIRECT);
    testee.bindQueueToExchange(queue, exchange);

    List<Message> receivedMessages = new ArrayList<>();
    testee.startConsuming(queue, receivedMessages::add);

    // Act
    testee.sendMessage(Message.create(exchange, message, ""));
    Thread.sleep(1_000);

    // Assert
    assertEquals(1, receivedMessages.size());
    assertEquals(message, receivedMessages.get(0).getMessage());
  }
}
