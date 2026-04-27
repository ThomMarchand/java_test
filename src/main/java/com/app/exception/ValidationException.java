package com.app.exception;

/**
 * Thrown when a user-supplied value fails a business validation rule
 * (e.g. missing required field, malformed email).
 *
 * <p>Maps to an HTTP {@code 400 Bad Request} response.</p>
 */
public class ValidationException extends RuntimeException {

  /**
   * @param message a human-readable description of the validation failure
   */
  public ValidationException(String message) {
    super(message);
  }
}
