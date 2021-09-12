package org.kryonite.service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kryonite.util.CustomObjectMapper;

import java.io.IOException;
import java.util.Queue;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
public class PublishMessageTask extends TimerTask {

  private static final ObjectMapper objectMapper = CustomObjectMapper.create();

  private final Queue<InternalMessage<?>> queue;
  private final Channel publishChannel;

  @Override
  public void run() {
    if (!publishChannel.isOpen()) {
      log.error("Channel is not open. Trying to reconnect!");
    } else {
      InternalMessage<?> internalMessage = queue.poll();
      if (internalMessage == null) {
        return;
      }

      try {
        Message<?> message = internalMessage.getMessage();
        byte[] body = objectMapper.writeValueAsBytes(message.getBody());
        publishChannel.basicPublish(
            message.getExchange(),
            message.getRoutingKey(),
            createBasicProperties(internalMessage),
            body
        );
      } catch (IOException e) {
        log.error("Got an exception while trying to publish message!", e);
        queue.add(internalMessage);
      }
    }
  }

  private AMQP.BasicProperties createBasicProperties(InternalMessage<?> internalMessage) {
    AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
    builder.headers(internalMessage.getHeaders());
    return builder.build();
  }
}
