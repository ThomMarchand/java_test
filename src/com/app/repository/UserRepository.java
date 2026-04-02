package com.app.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.app.model.User;

public class UserRepository {
  private Map<String, User> store = new HashMap<>();

  public void save(User user) {
    store.put(user.getId(), user);
  }

  public User findBy(String id) {
    return store.get(id);
  }

  public List<User> findAll() {
    return new ArrayList<>(store.values());
  }

  public boolean delete(String id) {
    return store.remove(id) != null;
  }

  public boolean exists(String id) {
    return store.containsKey(id);
  }
}
