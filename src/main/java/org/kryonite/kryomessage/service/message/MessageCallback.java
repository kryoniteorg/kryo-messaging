package org.kryonite.kryomessage.service.message;

@FunctionalInterface
public interface MessageCallback<T> {

  void messageReceived(Message<T> message);
}
