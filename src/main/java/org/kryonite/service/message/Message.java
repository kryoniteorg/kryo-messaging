package org.kryonite.service.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "create")
@AllArgsConstructor(staticName = "create")
public class Message<T> {

  private final String exchange;
  private final T body;
  private String routingKey = "";
}
