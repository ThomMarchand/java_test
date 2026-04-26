package com.app.model;

import java.util.UUID;

/**
 * Represents an application user.
 */
public class User {
  private String id;
  private String name;
  private String email;
  private int age;

  /**
   * Creates a new user with an auto-generated UUID.
   *
   * @param name  display name
   * @param email email address
   * @param age   age
   */
  public User(String name, String email, int age) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.email = email;
    this.age = age;
  }

  /**
   * Reconstructs a user from persistent storage with an existing id.
   *
   * @param id    existing unique identifier
   * @param name  display name
   * @param email email address
   * @param age   age
   */
  public User(String id, String name, String email, int age) {
    this.id = id;
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
