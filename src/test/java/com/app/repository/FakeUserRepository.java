package com.app.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.model.User;

public class FakeUserRepository extends UserRepository {

  private final Map<String, User> store = new HashMap<>();

  public FakeUserRepository() {
    super(null);
  }

  @Override
  public void save(User user) {
    store.put(user.getId(), user);
  }

  @Override
  public User findById(String id) {
    return store.get(id);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public boolean delete(String id) {
    return store.remove(id) != null;
  }
}
