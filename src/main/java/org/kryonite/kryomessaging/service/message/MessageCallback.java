package org.kryonite.kryomessaging.service.message;

@FunctionalInterface
public interface MessageCallback<T> {

  void messageReceived(Message<T> message);
}
