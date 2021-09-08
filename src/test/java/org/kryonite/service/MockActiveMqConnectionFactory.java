package org.kryonite.service;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.kryonite.api.ActiveMqConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Getter
public class MockActiveMqConnectionFactory implements ActiveMqConnectionFactory {

  private final ConnectionFactory connectionFactory = new MockConnectionFactory();

  @Override
  public Connection createConnection() throws IOException, TimeoutException {
    return connectionFactory.newConnection();
  }
}
