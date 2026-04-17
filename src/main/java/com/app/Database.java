package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private static Connection connection;

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
