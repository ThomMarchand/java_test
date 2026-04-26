package com.app.repository;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic CRUD contract for all repository implementations.
 *
 * @param <T> the entity type managed by this repository
 */
public interface Repository<T> {

  /**
   * Persists a new entity or replaces an existing one with the same id.
   *
   * @param entity the entity to save
   * @throws SQLException if the database operation fails
   */
  void save(T entity) throws SQLException;

  /**
   * Retrieves an entity by its unique identifier.
   *
   * @param id the entity's unique identifier
   * @return the matching entity, or {@code null} if none found
   * @throws SQLException if the database operation fails
   */
  T findById(String id) throws SQLException;

  /**
   * Returns all entities of this type from the database.
   *
   * @return a list of all entities, empty if none exist
   * @throws SQLException if the database operation fails
   */
  List<T> findAll() throws SQLException;

  /**
   * Deletes the entity with the given id.
   *
   * @param id the unique identifier of the entity to delete
   * @return {@code true} if a row was deleted, {@code false} if no entity matched
   * @throws SQLException if the database operation fails
   */
  boolean delete(String id) throws SQLException;
}
