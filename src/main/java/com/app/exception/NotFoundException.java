package com.app.exception;

/**
 * Thrown when a requested resource cannot be found in the database.
 *
 * <p>Maps to an HTTP {@code 404 Not Found} response.</p>
 */
public class NotFoundException extends RuntimeException {

  /**
   * @param message a human-readable description of the missing resource
   */
  public NotFoundException(String message) {
    super(message);
  }
}
