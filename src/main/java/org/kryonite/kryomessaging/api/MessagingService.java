package org.kryonite.kryomessaging.api;

import com.rabbitmq.client.BuiltinExchangeType;
import java.io.IOException;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;

public interface MessagingService {

  /**
   * Send a message with any class as a payload.
   *
   * @param message to send into the queue.
   */
  void sendMessage(Message<?> message);

  /**
   * Setup an exchange with a specific type and name.
   *
   * @param exchange     The exchanges name
   * @param exchangeType The exchanges {@link BuiltinExchangeType}
   * @throws IOException when an error is encountered during exchange setup.
   */
  void setupExchange(String exchange, BuiltinExchangeType exchangeType) throws IOException;

  /**
   * Bind a queue to an existing exchange.
   * <p>
   * If the given queue doesn't exist it will be created automatically.
   *
   * @param queue    The queues name
   * @param exchange The exchanges name
   * @throws IOException when an error is encountered during binding process.
   */
  void bindQueueToExchange(String queue, String exchange) throws IOException;

  /**
   * Bind a queue to an exchange with a specific routing key.
   * <p>
   * If the given queue doesn't exist it will be created automatically.
   *
   * @param queue      The queues name
   * @param exchange   The exchanges name
   * @param routingKey a exchange look at when deciding how to route the message.
   * @throws IOException when an error is encountered during binding process.
   */
  void bindQueueToExchange(String queue, String exchange, String routingKey) throws IOException;

  /**
   * Start consuming messages from a queue.
   *
   * @param queue           The queues name
   * @param messageCallback used for consuming the messages.
   * @param classOfCallback is the class of the messages that should be consumed.
   * @param <T>             is the type of the message.
   * @throws IOException when an error is encountered during consumption.
   */
  <T> void startConsuming(String queue, MessageCallback<T> messageCallback, Class<T> classOfCallback)
      throws IOException;
}
