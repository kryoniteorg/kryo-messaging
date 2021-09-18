package org.kryonite.kryomessage.service.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Person {

  private final String name;
  private final UUID id;
  private final int age;
  private final double salary;
}
