package org.kryonite.kryomessaging.service.model;

import java.util.UUID;
import lombok.Data;

@Data
public class Person {

  private final String name;
  private final UUID id;
  private final int age;
  private final double salary;
}
