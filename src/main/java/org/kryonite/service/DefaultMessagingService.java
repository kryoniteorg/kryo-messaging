package org.kryonite.service;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerShutdownSignalCallback;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.api.ActiveMqConnectionFactory;
import org.kryonite.api.MessagingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DefaultMessagingService implements MessagingService {

  private static final Map<String, Object> arguments = Map.of("x-queue-type", "quorum");

  private final Queue<Message> queue = new ConcurrentLinkedQueue<>();
  private final Channel channel;

  public DefaultMessagingService(ActiveMqConnectionFactory connectionFactory) throws IOException, TimeoutException {
    Connection connection = connectionFactory.createConnection();
    channel = connection.createChannel();

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new PublishMessageTask(queue, connection.createChannel()), 0, 50);
  }

  @Override
  public void sendMessage(Message message) {
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
  public void startConsuming(String queue, MessageCallback callback) throws IOException {
    DeliverCallback deliverCallback = (consumerTag, delivery) -> handleMessage(delivery, callback);
    ConsumerShutdownSignalCallback consumerShutdownSignalCallback = (consumerTag, sig) ->
        log.error("Consumer shutdown unexpectedly!", sig);

    channel.queueDeclare(queue, true, false, false, arguments);
    channel.basicConsume(queue, false, deliverCallback, consumerShutdownSignalCallback);
  }

  private void handleMessage(Delivery delivery, MessageCallback callback) throws IOException {
    if (!channel.isOpen()) {
      return;
    }
    Envelope envelope = delivery.getEnvelope();
    channel.basicAck(envelope.getDeliveryTag(), false);

    String exchange = envelope.getExchange();
    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
    String routingKey = envelope.getRoutingKey();
    callback.messageReceived(Message.create(exchange, message, routingKey));
  }
}
