package org.kryonite.kryomessaging.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomObjectMapperTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = CustomObjectMapper.create();
  }

  @Test
  void shouldSetupObjectMapperAndWorkModelsCorrectly() throws JsonProcessingException {
    // Arrange
    UUID uniqueId = UUID.randomUUID();
    TestModel testModel = TestModel.create(uniqueId);

    // Act
    String result = objectMapper.writeValueAsString(testModel);

    // Assert
    assertEquals("{\"uniqueId\":\"" + uniqueId + "\"}", result, "Mapped object does not map");
  }

  @Test
  void shouldSerializeAndDeserializeObject() throws JsonProcessingException {
    // Arrange
    UUID uniqueId = UUID.randomUUID();
    TestModel testModel = TestModel.create(uniqueId);

    // Act
    String serializedValue = objectMapper.writeValueAsString(testModel);
    TestModel result = objectMapper.readValue(serializedValue, TestModel.class);

    // Assert
    assertEquals(testModel, result, "Objects are not equal");
  }

  @Data(staticConstructor = "create")
  private static class TestModel {
    private final UUID uniqueId;
  }
}