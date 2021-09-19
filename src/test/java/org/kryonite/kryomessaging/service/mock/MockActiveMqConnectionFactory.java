package org.kryonite.kryomessaging.service.mock;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import org.kryonite.kryomessaging.api.ActiveMqConnectionFactory;

@Getter
public class MockActiveMqConnectionFactory implements ActiveMqConnectionFactory {

  private final ConnectionFactory connectionFactory = new MockConnectionFactory();

  @Override
  public Connection createConnection() throws IOException, TimeoutException {
    return connectionFactory.newConnection();
  }
}
