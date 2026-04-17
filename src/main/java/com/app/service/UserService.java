package com.app.service;

import java.sql.SQLException;
import java.util.List;

import com.app.exception.ValidationException;
import com.app.model.User;
import com.app.repository.UserRepository;

public class UserService {
private UserRepository repository;

  public UserService(UserRepository repository) {
    this.repository = repository;
  }

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

  public List<User> findAllUsers() throws SQLException {
    return repository.findAll();
  }

  public boolean deleteUser(String id) throws SQLException {
    return repository.delete(id);
  }

  public User updateUser(String id, String name, String email, int age) throws SQLException {
    User user = repository.findById(id);

    user.setName(name);
    user.setEmail(email);
    user.setAge(age);

    repository.save(user);

    return user;
  }
}
