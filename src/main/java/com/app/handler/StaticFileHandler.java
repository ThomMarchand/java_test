package com.app.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * HTTP handler that serves static files from the {@code static/} directory.
 *
 * <p>Requests to {@code /} are redirected to {@code /index.html}.
 * Returns {@code 404} implicitly if the file does not exist.</p>
 */
public class StaticFileHandler implements HttpHandler {

  /**
   * Resolves the requested URI to a file inside {@code static/} and writes
   * its content to the response with the appropriate {@code Content-Type} header.
   *
   * @param exchange the HTTP exchange containing request and response
   * @throws IOException if reading the file or writing the response fails
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String uri = exchange.getRequestURI().getPath();

    if (uri.equals("/")) {
      uri = "/index.html";
    }

    Path filePath = Paths.get("static" + uri);

    if (Files.exists(filePath)) {
      byte[] bytes = Files.readAllBytes(filePath);
      String contentType = getContentType(uri);

      exchange.getResponseHeaders().set("Content-type", contentType);
      exchange.sendResponseHeaders(200, bytes.length);

      OutputStream out = exchange.getResponseBody();
      out.write(bytes);
      out.close();
    }
  }

  private String getContentType(String path) {
    if (path.endsWith(".html")) return "text/html";
    if (path.endsWith(".css")) return "text/css";
    if (path.endsWith(".js")) return "application/javascript";

    return "text/plain";
  }
}
