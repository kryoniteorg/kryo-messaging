package org.kryonite.kryomessaging.api;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ActiveMqConnectionFactory {

  ConnectionFactory getConnectionFactory();

  Connection createConnection() throws IOException, TimeoutException;
}
