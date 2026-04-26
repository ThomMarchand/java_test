package com.app.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Template implementation of {@link Repository} that handles common SQL operations.
 *
 * <p>Subclasses provide the table name, the INSERT statement, parameter binding,
 * and a {@link RowMapper} — this class takes care of the rest.</p>
 *
 * @param <T> the entity type managed by this repository
 */
public abstract class AbstractRepository<T> implements Repository<T> {
  private final Connection connection;

  /**
   * @param connection the shared database connection
   */
  protected AbstractRepository(Connection connection) {
    this.connection = connection;
  }

  /**
   * @return the database table name for this entity (e.g. {@code "users"})
   */
  protected abstract String getTableName();

  /**
   * @return the INSERT (or INSERT OR REPLACE) SQL statement for this entity
   */
  protected abstract String getInsertSql();

  /**
   * Binds the entity's fields onto the given prepared statement.
   *
   * @param stmt   the prepared statement produced from {@link #getInsertSql()}
   * @param entity the entity whose fields to bind
   * @throws SQLException if a field cannot be bound
   */
  protected abstract void bindInsertParams(PreparedStatement stmt, T entity) throws SQLException;

  /**
   * @return a {@link RowMapper} that converts a result-set row into an entity
   */
  protected abstract RowMapper<T> getRowMapper();

  @Override
  public void save(T entity) throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(getInsertSql())) {
      bindInsertParams(stmt, entity);
      stmt.executeUpdate();
    }
  }

  @Override
  public T findById(String id) throws SQLException {
    String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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

    try (Statement stmt = connection.createStatement()) {
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

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, id);

      return stmt.executeUpdate() > 0;
    }
  }
}
