package com.app.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.app.model.User;

public class UserRepository extends AbstractRepository<User> {
  public UserRepository(Connection connection) {
    super(connection);
  }

  @Override
  protected String getTableName() {
    return "users";
  }

  @Override
  protected String getInsertSql() {
    return "INSERT OR REPLACE INTO " + getTableName() + " (id, name, email, age) VALUES (?, ?, ?, ?)";
  }

  @Override
  protected void bindInsertParams(PreparedStatement stmt, User user) throws SQLException {
    stmt.setString(1, user.getId());
    stmt.setString(2, user.getName());
    stmt.setString(3, user.getEmail());
    stmt.setInt(4, user.getAge());
  }

  @Override
  protected RowMapper<User> getRowMapper() {
    return rs -> new User(
      rs.getString("id"),
      rs.getString("name"),
      rs.getString("email"),
      rs.getInt("age")
    );
  }
}
