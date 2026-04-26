package com.app.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Functional interface that maps a {@link ResultSet} row to a domain object.
 *
 * @param <T> the target type to produce from each row
 */
public interface RowMapper<T> {

  /**
   * Maps the current row of the given {@link ResultSet} to an instance of {@code T}.
   * The cursor must already be positioned on the row to read (i.e. after a call to
   * {@link ResultSet#next()}).
   *
   * @param rs the result set positioned on the row to map
   * @return the mapped object
   * @throws SQLException if a column cannot be read
   */
  T map(ResultSet rs) throws SQLException;
}
