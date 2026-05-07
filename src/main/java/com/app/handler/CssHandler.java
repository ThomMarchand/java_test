package com.app.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.app.renderer.TemplateRenderer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * HTTP handler that renders and serves the aggregated stylesheet via {@code styles.jte}.
 */
public class CssHandler implements HttpHandler {

  private final TemplateRenderer renderer;

  /**
   * @param renderer the JTE template renderer
   */
  public CssHandler(TemplateRenderer renderer) {
    this.renderer = renderer;
  }

  /**
   * Renders {@code styles.jte} and writes the result with {@code Content-Type: text/css}.
   *
   * @param exchange the HTTP exchange containing request and response
   * @throws IOException if writing the response fails
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
      String css = renderer.render("styles.jte", Map.of());
      byte[] bytes = css.getBytes(StandardCharsets.UTF_8);

      exchange.getResponseHeaders().set("Content-Type", "text/css; charset=utf-8");
      exchange.sendResponseHeaders(200, bytes.length);

      try (OutputStream out = exchange.getResponseBody()) {
        out.write(bytes);
      }
    } catch (Exception e) {
      String error = e.getMessage();
      byte[] bytes = error.getBytes(StandardCharsets.UTF_8);

      exchange.sendResponseHeaders(500, bytes.length);

      try (OutputStream out = exchange.getResponseBody()) {
        out.write(bytes);
      }
    }
  }
}
