package org.kryonite.kryomessage.service.message;

import lombok.Data;

import java.util.Map;

@Data(staticConstructor = "create")
public class InternalMessage<T> {

  private final Message<T> message;
  private final Map<String, Object> headers;
}
