package com.app.service;

import java.sql.SQLException;
import java.util.List;

import com.app.exception.ValidationException;
import com.app.model.User;
import com.app.repository.FakeUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  private UserService service;

  @BeforeEach
  void setUp() {
    service = new UserService(new FakeUserRepository());
  }

  @Test
  void createUser_shouldReturnUserWithCorrectFields() throws SQLException {
    User user = service.createUser("Alice", "alice@example.com", 30);

    assertNotNull(user.getId());
    assertEquals("Alice", user.getName());
    assertEquals("alice@example.com", user.getEmail());
    assertEquals(30, user.getAge());
  }

  @Test
  void createUser_shouldThrowValidationException_whenNameIsNull() {
    assertThrows(ValidationException.class, () ->
      service.createUser(null, "alice@example.com", 30)
    );
  }

  @Test
  void createUser_shouldThrowValidationException_whenNameIsEmpty() {
    assertThrows(ValidationException.class, () ->
      service.createUser("", "alice@example.com", 30)
    );
  }

  @Test
  void createUser_shouldThrowValidationException_whenEmailIsNull() {
    assertThrows(ValidationException.class, () ->
      service.createUser("Alice", null, 30)
    );
  }

  @Test
  void createUser_shouldThrowValidationException_whenEmailHasNoAtSign() {
    assertThrows(ValidationException.class, () ->
      service.createUser("Alice", "aliceexample.com", 30)
    );
  }

  @Test
  void findAllUsers_shouldReturnAllSavedUsers() throws SQLException {
    service.createUser("Alice", "alice@example.com", 30);
    service.createUser("Bob", "bob@example.com", 25);

    List<User> users = service.findAllUsers();

    assertEquals(2, users.size());
  }

  @Test
  void deleteUser_shouldReturnTrue_whenUserExists() throws SQLException {
    User user = service.createUser("Alice", "alice@example.com", 30);

    assertTrue(service.deleteUser(user.getId()));
  }

  @Test
  void deleteUser_shouldReturnFalse_whenUserDoesNotExist() throws SQLException {
    assertFalse(service.deleteUser("non-existing-id"));
  }

  @Test
  void updateUser_shouldReturnUserWithUpdatedFields() throws SQLException {
    User user = service.createUser("Alice", "alice@example.com", 30);

    User updated = service.updateUser(user.getId(), "Bob", "bob@example.com", 25);

    assertEquals("Bob", updated.getName());
    assertEquals("bob@example.com", updated.getEmail());
    assertEquals(25, updated.getAge());
  }
}
