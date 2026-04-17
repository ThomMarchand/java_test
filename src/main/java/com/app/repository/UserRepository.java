package com.app.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.app.Database;
import com.app.model.User;
import com.app.util.Logger;

public class UserRepository {
  public void save(User user) throws SQLException {
    String sql = """
      INSERT OR REPLACE INTO users(
        id, name, email, age
      ) VALUES (?, ?, ?, ?)
    """;

    try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
      stmt.setString(1, user.getId());
      stmt.setString(2, user.getName());
      stmt.setString(3, user.getEmail());
      stmt.setInt(4, user.getAge());
      stmt.executeUpdate();
    }
  }

  public User findById(String id) throws SQLException {
    String sql = "SELECT * FROM users WHERE id = ?";

    try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
      stmt.setString(1, id);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return mapRow(rs);
      }

      return null;
    }
  }

  public List<User> findAll() throws SQLException {
    String sql = "SELECT * FROM users";
    List<User> users = new ArrayList<>();

    try (Statement stmt = Database.getConnection().createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        users.add(mapRow(rs));
      }
    }

    return users;
  }

  public boolean delete(String id) throws SQLException {
    Logger.printLn("id recu dans le reporsitory =>"+ id);
    String sql = "DELETE FROM users WHERE id = ?";

    Logger.printLn("retour bdd =>" + sql);

    try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
      stmt.setString(1, id);

      return stmt.executeUpdate() > 0;
    }
  }

  private User mapRow(ResultSet rs) throws SQLException {
    return new User(
      rs.getString("id"), 
      rs.getString("name"), 
      rs.getString("email"), 
      rs.getInt("age")
    );
  }
}
