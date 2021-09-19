package org.kryonite.kryomessaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kryonite.kryomessaging.api.ActiveMqConnectionFactory;

class DefaultActiveMqConnectionFactoryTest {

  @Test
  void shouldSetupConnectionFactory() {
    String username = "testee";
    String password = "testee";

    // Arrange
    ActiveMqConnectionFactory testee = new DefaultActiveMqConnectionFactory(
        List.of(new Address("localhost", 5672)),
        username,
        password
    );

    // Act
    ConnectionFactory connectionFactory = testee.getConnectionFactory();

    // Assert
    assertEquals(username, connectionFactory.getUsername());
    assertEquals(password, connectionFactory.getPassword());
  }
}
