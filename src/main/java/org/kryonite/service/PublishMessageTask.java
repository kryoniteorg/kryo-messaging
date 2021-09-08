package org.kryonite.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
public class PublishMessageTask extends TimerTask {

  private final Queue<Message> queue;
  private final Channel publishChannel;

  @Override
  public void run() {
    if (!publishChannel.isOpen()) {
      log.error("Channel is not open. Trying to reconnect!");
    } else {
      Message message = queue.poll();
      if (message == null) {
        return;
      }

      try {
        byte[] body = message.getMessage().getBytes(StandardCharsets.UTF_8);
        publishChannel.basicPublish(message.getExchange(), message.getRoutingKey(), null, body);
      } catch (IOException e) {
        log.error("Got an exception while trying to publish message!");
        queue.add(message);
      }
    }
  }
}
