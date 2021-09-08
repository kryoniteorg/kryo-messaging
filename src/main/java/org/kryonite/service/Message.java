package org.kryonite.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class Message {

  private final String exchange;
  private final String message;
  private String routingKey;
}
