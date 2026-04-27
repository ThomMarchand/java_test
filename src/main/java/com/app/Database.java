package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the singleton SQLite connection and initialises the database schema.
 */
public class Database {
  private static Connection connection;

  /**
   * Returns the shared {@link Connection}, creating it on first call.
   * Also creates the {@code users} table if it does not already exist.
   *
   * @return an open connection to {@code crud.sqlite}
   * @throws SQLException if the driver is missing or the connection cannot be established
   */
  public static Connection getConnection() throws SQLException {
    if (connection == null || connection.isClosed()) {
      try {
        Class.forName("org.sqlite.JDBC");
      } catch (ClassNotFoundException e) {
        throw new SQLException("SQLite JDBC driver not found", e);
      }
      connection = DriverManager.getConnection("jdbc:sqlite:crud.sqlite");

      initSchema();
    }

    return connection;
  }

  private static void initSchema() throws SQLException {
    String sql = """
      CREATE TABLE IF NOT EXISTS users(
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        email TEXT NOT NULL,
        age INTEGER NOT NULL
      )
    """;

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(sql);
    }
  }
}
