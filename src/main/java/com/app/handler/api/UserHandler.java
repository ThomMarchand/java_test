package com.app.handler.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.app.exception.NotFoundException;
import com.app.exception.ValidationException;
import com.app.model.User;
import com.app.service.UserService;

/**
 * HTTP handler that routes {@code /api/users} requests to the {@link UserService}.
 *
 * <p>Supported routes:</p>
 * <ul>
 *   <li>{@code GET  /api/users}        — list all users</li>
 *   <li>{@code POST /api/users}        — create a user</li>
 *   <li>{@code PUT  /api/users/{id}}   — update a user</li>
 *   <li>{@code DELETE /api/users/{id}} — delete a user</li>
 * </ul>
 */
public class UserHandler implements HttpHandler {
  private UserService service;

  /**
   * @param service the service handling user business logic
   */
  public UserHandler(UserService service) {
    this.service = service;
  }

  /**
   * Dispatches the incoming request to the appropriate handler method based on
   * the HTTP method and path. Responds with {@code 405 Method Not Allowed} for
   * unrecognised routes.
   *
   * @param exchange the HTTP exchange containing request and response
   * @throws IOException if writing the response fails
   */
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String method = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();

    if (method.equals("GET") && path.equals("/api/users")) {
      handleGetAll(exchange);

    } else if (method.equals("POST") && path.equals("/api/users")) {
      handleCreate(exchange);

    } else if (method.equals("DELETE") && path.startsWith("/api/users/")) {
      String id = path.replace("/api/users/", "");

      handleDelete(exchange, id);

    } else if (method.equals("PUT") && path.startsWith("/api/users/")) {
      String id = path.replace("/api/users/", "");

      handleUpdate(exchange, id);

    } else {
      sendResponse(exchange, 405, "Method Not Allowed");
    }
  }

  private void handleGetAll(HttpExchange exchange) throws IOException {
    try {
      
    
    List<User> users = service.findAllUsers();

    StringBuilder json = new StringBuilder("[");

    for (int i = 0; i < users.size(); i++) {
      User u = users.get(i);

      json.append("{")
          .append("\"id\":\"").append(u.getId()).append("\",")
          .append("\"name\":\"").append(u.getName()).append("\",")
          .append("\"email\":\"").append(u.getEmail()).append("\",")
          .append("\"age\":").append(u.getAge())
          .append("}");
      if (i < users.size() -1) json.append(",");
    }

    json.append("]");

    sendJsonResponse(exchange, 200, json.toString());
    } catch (SQLException e) {
      sendResponse(exchange, 500, e.getMessage());
    }
  }

  private void handleCreate(HttpExchange exchange) throws IOException {
    Map<String, String> params = parseBody(exchange);
    
    try {
      User user = service.createUser(
        params.getOrDefault("name", ""),
        params.getOrDefault("email", ""),
        Integer.parseInt(params.getOrDefault("age", "0"))
      );
      String json = "{\"id\":\"" + user.getId() + "\",\"name\":\"" + user.getName() + "\"}";

      sendJsonResponse(exchange, 201, json);
    } catch (ValidationException e) {
      sendResponse(exchange, 400, e.getMessage());
    } catch (SQLException e) {
      sendResponse(exchange, 500, e.getMessage());
    }
  }

  private void handleDelete(HttpExchange exchange, String id) throws IOException {
    try {
      
    
      boolean deleted = service.deleteUser(id);

      if (deleted) {
        sendResponse(exchange, 200, "User deleted");

      } else {
        sendResponse(exchange, 404, "User not found");
      }
    } catch (SQLException e) {
      sendResponse(exchange, 500, e.getMessage());
    }
  }

  private void handleUpdate(HttpExchange exchange, String id) throws IOException {
    Map<String, String> params = parseBody(exchange);

    try {
      User user = service.updateUser(
        id,
        params.getOrDefault("name", ""),
        params.getOrDefault("email", ""),
        Integer.parseInt(params.getOrDefault("age", ""))
      );
      String json = "{\"id\":\"" + user.getId() + "\",\"name\":\"" + user.getName() + "\"}";

      sendJsonResponse(exchange, 200, json);
    } catch (NotFoundException e) {
      sendResponse(exchange, 404, e.getMessage());

    } catch (ValidationException e) {
      sendResponse(exchange, 400, e.getMessage());
    } catch (SQLException e) {
      sendResponse(exchange, 500, e.getMessage());
    }
  }
  
  
  private void sendJsonResponse(HttpExchange exchange, int status, String body) throws IOException {
    exchange.getResponseHeaders().set("Content-type", "application/json");

    sendResponse(exchange, status, body);
  }

  private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(status, bytes.length);

    OutputStream out = exchange.getResponseBody();

    out.write(bytes);
    out.close();
  }

  private Map<String, String> parseBody(HttpExchange exchange) throws IOException {
    InputStream body = exchange.getRequestBody();
    String raw = new String(body.readAllBytes(), StandardCharsets.UTF_8);
    Map<String, String> params = new HashMap<>();

    for (String param : raw.split("&")) {
      String[] pair = param.split("=");

      if (pair.length < 2) continue;

      params.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
    }

    return params;
  }
}
