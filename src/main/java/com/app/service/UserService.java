package com.app.service;

import java.sql.SQLException;
import java.util.List;

import com.app.exception.NotFoundException;
import com.app.exception.ValidationException;
import com.app.model.User;
import com.app.repository.UserRepository;

/**
 * Business logic layer for user management.
 *
 * <p>Bridges the HTTP layer ({@link com.com.app.handler.api.UserHandler}) and the
 * persistence layer ({@link com.app.repository.UserRepository}), enforcing
 * validation rules before any data operation.</p>
 */
public class UserService {
private UserRepository repository;

  /**
   * @param repository the repository used for user persistence
   */
  public UserService(UserRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates and persists a new user after validating the input fields.
   *
   * @param name  user's display name (must be non-null and non-empty)
   * @param email user's email address (must contain {@code @})
   * @param age   user's age
   * @return the newly created {@link User} with a generated id
   * @throws ValidationException if {@code name} or {@code email} is invalid
   * @throws SQLException        if the database operation fails
   */
  public User createUser(String name, String email, int age) throws SQLException {
    if (name == null || name.isEmpty()) {
      throw new ValidationException("Missing required field name");
    }

    if (email == null || !email.contains("@")) {
      throw new ValidationException("Missing required field email");
    }

    User user = new User(name, email, age);

    repository.save(user);

    return user;
  }

  /**
   * Returns all users stored in the database.
   *
   * @return a list of all users, empty if none exist
   * @throws SQLException if the database operation fails
   */
  public List<User> findAllUsers() throws SQLException {
    return repository.findAll();
  }

  /**
   * Deletes the user with the given id.
   *
   * @param id the unique identifier of the user to delete
   * @return {@code true} if a row was deleted, {@code false} if no user matched
   * @throws SQLException if the database operation fails
   */
  public boolean deleteUser(String id) throws SQLException {
    return repository.delete(id);
  }

  /**
   * Updates an existing user's fields after validating the input.
   *
   * @param id    the unique identifier of the user to update
   * @param name  new display name (must be non-null and non-empty)
   * @param email new email address (must contain {@code @})
   * @param age   new age
   * @return the updated {@link User}
   * @throws ValidationException if {@code name} or {@code email} is invalid
   * @throws NotFoundException   if no user exists with the given {@code id}
   * @throws SQLException        if the database operation fails
   */
  public User updateUser(String id, String name, String email, int age) throws SQLException {
    if (name == null || name.isEmpty()) {
      throw new ValidationException("Missing required field name");
    }

    if (email == null || !email.contains("@")) {
      throw new ValidationException("Missing required field email");
    }

    User user = repository.findById(id);

    if (user == null) throw new NotFoundException("User not found");

    user.setName(name);
    user.setEmail(email);
    user.setAge(age);

    repository.save(user);

    return user;
  }
}
