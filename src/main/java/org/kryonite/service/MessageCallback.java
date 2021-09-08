package org.kryonite.service;

@FunctionalInterface
public interface MessageCallback {

  void messageReceived(Message message);
}
