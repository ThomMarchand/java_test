package com.app.handler.template;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.app.model.User;
import com.app.renderer.TemplateRenderer;
import com.app.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class LandingTemplateHandler implements HttpHandler {
  private final UserService userService;
  private final TemplateRenderer renderer;

  public LandingTemplateHandler(UserService userService, TemplateRenderer renderer) {
    this.userService = userService;
    this.renderer = renderer;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    try {
        List<User> users = userService.findAllUsers();
        String html = renderer.renderUsers(users);
        
        exchange.getResponseHeaders().set("Content-type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, html.getBytes().length);
        
        try (OutputStream out = exchange.getResponseBody()) {
          out.write(html.getBytes());
        }
    } catch (Exception e) {
      e.printStackTrace();
      String error = e.getMessage();

      exchange.sendResponseHeaders(500, error.getBytes().length);

      try (OutputStream os = exchange.getResponseBody()) {
          os.write(error.getBytes());
      }
    }
  }
}
