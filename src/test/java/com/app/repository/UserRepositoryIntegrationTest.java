package com.app.repository;

import com.app.model.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryIntegrationTest {

  private Connection connection;
  private UserRepository repository;

  @BeforeEach
  void setUp() throws SQLException {
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("""
          CREATE TABLE IF NOT EXISTS users (
            id    TEXT    PRIMARY KEY,
            name  TEXT    NOT NULL,
            email TEXT    NOT NULL,
            age   INTEGER NOT NULL
          )
          """);
    }
    repository = new UserRepository(connection);
  }

  @AfterEach
  void tearDown() throws SQLException {
    connection.close();
  }

  @Test
  void save_shouldPersistUser() throws SQLException {
    User user = new User("1", "Alice", "alice@example.com", 30);

    repository.save(user);

    User found = repository.findById("1");
    assertNotNull(found);
    assertEquals("Alice", found.getName());
    assertEquals("alice@example.com", found.getEmail());
    assertEquals(30, found.getAge());
  }

  @Test
  void save_shouldReplaceExistingUserWithSameId() throws SQLException {
    User original = new User("1", "Alice", "alice@example.com", 30);
    User updated  = new User("1", "Alice Updated", "new@example.com", 31);

    repository.save(original);
    repository.save(updated);

    User found = repository.findById("1");
    assertEquals("Alice Updated", found.getName());
    assertEquals("new@example.com", found.getEmail());
    assertEquals(31, found.getAge());
  }

  @Test
  void findById_shouldReturnNull_whenUserDoesNotExist() throws SQLException {
    assertNull(repository.findById("nonexistent"));
  }

  @Test
  void findAll_shouldReturnAllUsers() throws SQLException {
    repository.save(new User("1", "Alice", "alice@example.com", 30));
    repository.save(new User("2", "Bob",   "bob@example.com",   25));

    List<User> users = repository.findAll();

    assertEquals(2, users.size());
  }

  @Test
  void findAll_shouldReturnEmptyList_whenNoUsers() throws SQLException {
    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  void delete_shouldRemoveUser_andReturnTrue() throws SQLException {
    repository.save(new User("1", "Alice", "alice@example.com", 30));

    boolean deleted = repository.delete("1");

    assertTrue(deleted);
    assertNull(repository.findById("1"));
  }

  @Test
  void delete_shouldReturnFalse_whenUserDoesNotExist() throws SQLException {
    assertFalse(repository.delete("nonexistent"));
  }
}
