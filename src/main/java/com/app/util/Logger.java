package com.app.util;

/**
 * Minimal utility for console logging.
 */
public class Logger {

  /**
   * Prints {@code message} to standard output followed by a newline.
   *
   * @param <T>     the type of the message
   * @param message the value to print
   */
  public static <T> void printLn(T message) {
    System.out.println(message);
  }
}
