package com.app.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.app.model.User;
import com.app.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class UserHandler implements HttpHandler {
  private UserService service;

  public UserHandler(UserService service) {
    this.service = service;
  }

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
    } catch (IllegalArgumentException e) {
      sendResponse(exchange, 400, e.getMessage());
    }
  }

  private void handleDelete(HttpExchange exchange, String id) throws IOException {
    boolean deleted = service.deleteUser(id);

    if (deleted) {
      sendResponse(exchange, 200, "User deleted");

    } else {
      sendResponse(exchange, 404, "User not found");
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
    } catch (Exception e) {
      sendResponse(exchange, 404, e.getMessage());
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
