package org.kryonite.kryomessaging.service.message;

import java.util.Map;
import lombok.Data;

@Data(staticConstructor = "create")
public class InternalMessage<T> {

  private final Message<T> message;
  private final Map<String, Object> headers;
}
