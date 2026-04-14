package com.app.model;

import java.util.UUID;

public class User {
  private String id;
  private String name;
  private String email;
  private int age;

  public User(String name, String email, int age) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.email = email;
    this.age = age;
  }

  public void setName(String name) { this.name = name; }
  public void setEmail(String email) { this.email = email; }
  public void setAge(int age) { this.age = age; }

  public String getId() { return this.id; }
  public String getName() { return this.name; }
  public String getEmail() { return this.email; }
  public int getAge() { return this.age; }

  @Override
  public String toString() {
    return "User: { id: " + id + ", name: " + name + ", email: " + email + ", age: " + age + " }";
  }
}
