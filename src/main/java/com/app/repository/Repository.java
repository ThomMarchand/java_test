package com.app.repository;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {
  void save(T entity) throws SQLException;
  T findById(String id) throws SQLException;
  List<T> findAll() throws SQLException;
  boolean delete(String id) throws SQLException;
}
