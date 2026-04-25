package com.app.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.app.Database;

public abstract class AbstractRepository<T> implements Repository<T> {
  protected abstract String getTableName();
  protected abstract String getInsertSql();
  protected abstract void bindInsertParams(PreparedStatement stmt, T entity) throws SQLException;
  protected abstract RowMapper<T> getRowMapper();

  @Override
  public void save(T entity) throws SQLException {
    try (PreparedStatement stmt = Database.getConnection().prepareStatement(getInsertSql())) {}
  }

  @Override
  public T findById(String id) throws SQLException {
    String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";

    try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
      stmt.setString(1, id);

      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        return getRowMapper().map(rs);
      }

      return null;
    }
  }

  @Override
  public List<T> findAll() throws SQLException {
    String sql = "SELECT * FROM " + getTableName();
    List<T> results = new ArrayList<>();

    try (Statement stmt = Database.getConnection().createStatement()) {
      ResultSet rs = stmt.executeQuery(sql);

      while (rs.next()) {
        results.add(getRowMapper().map(rs));
      }
    }

    return results;
  }

  @Override
  public boolean delete(String id) throws SQLException {
    String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";

    try (PreparedStatement stmt = Database.getConnection().prepareStatement(sql)) {
      stmt.setString(1, id);

      return stmt.executeUpdate() > 0;
    }
  }
}
