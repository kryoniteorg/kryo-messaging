package org.kryonite.service;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import org.kryonite.api.ActiveMqConnectionFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Getter
public class DefaultActiveMqConnectionFactory implements ActiveMqConnectionFactory {

  private final ConnectionFactory connectionFactory;
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
