package org.kryonite.kryomessaging.service;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.AccessLevel;
import lombok.Getter;
import org.kryonite.kryomessaging.api.ActiveMqConnectionFactory;

@Getter
public class DefaultActiveMqConnectionFactory implements ActiveMqConnectionFactory {

  private final ConnectionFactory connectionFactory;
  @Getter(AccessLevel.NONE)
  private final List<Address> addresses;

  public DefaultActiveMqConnectionFactory(List<Address> addresses, String username, String password) {
    connectionFactory = new ConnectionFactory();
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setNetworkRecoveryInterval(250);

    this.addresses = addresses;
  }

  @Override
  public Connection createConnection() throws IOException, TimeoutException {
    return connectionFactory.newConnection(addresses);
  }
}
