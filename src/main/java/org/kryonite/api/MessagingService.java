package org.kryonite.api;

import com.rabbitmq.client.BuiltinExchangeType;
import org.kryonite.service.message.Message;
import org.kryonite.service.message.MessageCallback;

import java.io.IOException;

public interface MessagingService {

  void sendMessage(Message<?> message);

  void setupExchange(String exchange, BuiltinExchangeType exchangeType) throws IOException;

  void bindQueueToExchange(String queue, String exchange) throws IOException;

  void bindQueueToExchange(String queue, String exchange, String routingKey) throws IOException;

  <T> void startConsuming(String queue, MessageCallback<T> messageCallback, Class<T> classOfCallback)
      throws IOException;
}
