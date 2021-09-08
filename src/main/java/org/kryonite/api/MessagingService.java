package org.kryonite.api;

import com.rabbitmq.client.BuiltinExchangeType;
import org.kryonite.service.Message;
import org.kryonite.service.MessageCallback;

import java.io.IOException;

public interface MessagingService {

  void sendMessage(Message message);

  void setupExchange(String exchange, BuiltinExchangeType exchangeType) throws IOException;

  void bindQueueToExchange(String queue, String exchange) throws IOException;

  void bindQueueToExchange(String queue, String exchange, String routingKey) throws IOException;

  void startConsuming(String queue, MessageCallback messageCallback) throws IOException;
}
