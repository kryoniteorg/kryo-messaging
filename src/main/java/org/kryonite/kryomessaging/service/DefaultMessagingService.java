package org.kryonite.kryomessaging.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.kryomessaging.api.ActiveMqConnectionFactory;
import org.kryonite.kryomessaging.api.MessagingService;
import org.kryonite.kryomessaging.service.message.InternalMessage;
import org.kryonite.kryomessaging.service.message.Message;
import org.kryonite.kryomessaging.service.message.MessageCallback;
import org.kryonite.kryomessaging.service.message.PublishMessageTask;
import org.kryonite.kryomessaging.util.CustomObjectMapper;

@Slf4j
public class DefaultMessagingService implements MessagingService {

  protected static final String RETRY_HEADER = "x-retries-left";
  protected static final int DEFAULT_RETRY_COUNT = 5;

  private static final Map<String, Object> arguments = Map.of(
      "x-queue-type", "quorum",
      "x-message-ttl", Duration.ofMinutes(10).getSeconds()
  );
  private static final ObjectMapper objectMapper = CustomObjectMapper.create();

  private final Queue<InternalMessage<?>> queue = new ConcurrentLinkedQueue<>();
  private final Channel channel;

  @SuppressWarnings("squid:S2095")
  public DefaultMessagingService(ActiveMqConnectionFactory connectionFactory) throws IOException, TimeoutException {
    Connection connection = connectionFactory.createConnection();
    channel = connection.createChannel();

    Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new PublishMessageTask(queue, connection.createChannel()), 0, 50);
  }

  @Override
  public void sendMessage(Message<?> message) {
    sendMessage(InternalMessage.create(message, Collections.emptyMap()));
  }

  private void sendMessage(InternalMessage<?> message) {
    queue.add(message);
  }

  @Override
  public void setupExchange(String exchange, BuiltinExchangeType exchangeType) throws IOException {
    channel.exchangeDeclare(exchange, exchangeType);
  }

  @Override
  public void bindQueueToExchange(String queue, String exchange) throws IOException {
    bindQueueToExchange(queue, exchange, "");
  }

  @Override
  public void bindQueueToExchange(String queue, String exchange, String routingKey) throws IOException {
    channel.queueDeclare(queue, true, false, false, arguments);
    channel.queueBind(queue, exchange, routingKey);
  }

  @Override
  public <T> void startConsuming(String queue,
                                 MessageCallback<T> callback,
                                 Class<T> classOfCallback) throws IOException {
    DeliverCallback deliverCallback = (consumerTag, delivery) -> handleMessage(delivery, callback, classOfCallback);
    ConsumerShutdownSignalCallback consumerShutdownSignalCallback = (consumerTag, sig) ->
        log.error("Consumer shutdown unexpectedly!", sig);

    channel.queueDeclare(queue, true, false, false, arguments);
    channel.basicConsume(queue, true, deliverCallback, consumerShutdownSignalCallback);
  }

  private <T> void handleMessage(Delivery delivery,
                                 MessageCallback<T> callback,
                                 Class<T> classOfCallback) {
    if (!channel.isOpen()) {
      return;
    }
    Envelope envelope = delivery.getEnvelope();

    String exchange = envelope.getExchange();
    try {
      T body = objectMapper.readValue(delivery.getBody(), classOfCallback);
      String routingKey = envelope.getRoutingKey();
      Message<T> message = Message.create(exchange, body, routingKey);
      consumeMessage(delivery, callback, message);
    } catch (IOException exception) {
      log.error("Failed to consume message!", exception);
    }
  }

  private <T> void consumeMessage(Delivery delivery, MessageCallback<T> callback, Message<T> message) {
    try {
      callback.messageReceived(message);
    } catch (Exception exception) {
      log.error("Could not consume message!", exception);
      int retriesLeft = (Integer) delivery.getProperties().getHeaders()
          .getOrDefault(RETRY_HEADER, DEFAULT_RETRY_COUNT) - 1;
      if (retriesLeft <= 0) {
        log.error("Dropping message {} because it failed {} times!", message, DEFAULT_RETRY_COUNT);
      } else {
        queue.add(InternalMessage.create(message, Map.of(RETRY_HEADER, retriesLeft)));
      }
    }
  }
}
