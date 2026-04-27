package com.app.handler;

import com.app.repository.FakeUserRepository;
import com.app.service.UserService;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class UserHandlerIntegrationTest {

  private HttpServer server;
  private HttpClient client;
  private String baseUrl;
  private UserService service;
  private final List<HttpServer> serversToStop = new ArrayList<>();

  @BeforeEach
  void setUp() throws IOException {
    service = new UserService(new FakeUserRepository());

    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/api/users", new UserHandler(service));
    server.start();

    baseUrl = "http://localhost:" + server.getAddress().getPort();
    client = HttpClient.newHttpClient();
  }

  @AfterEach
  void tearDown() {
    server.stop(0);
    serversToStop.forEach(s -> s.stop(0));
    serversToStop.clear();
  }

  @Test
  void getAll_shouldReturn200WithEmptyArray() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder().uri(URI.create(baseUrl + "/api/users")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(200, response.statusCode());
    assertEquals("[]", response.body());
  }

  @Test
  void getAll_shouldReturn200WithUsers() throws IOException, InterruptedException, SQLException {
    service.createUser("Alice", "alice@example.com", 30);

    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder().uri(URI.create(baseUrl + "/api/users")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("\"name\":\"Alice\""));
    assertTrue(response.body().contains("\"email\":\"alice@example.com\""));
  }

  @Test
  void create_shouldReturn201_whenUserIsValid() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users"))
            .POST(HttpRequest.BodyPublishers.ofString("name=Alice&email=alice%40example.com&age=30"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(201, response.statusCode());
    assertTrue(response.body().contains("\"name\":\"Alice\""));
  }

  @Test
  void create_shouldReturn400_whenNameIsMissing() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users"))
            .POST(HttpRequest.BodyPublishers.ofString("email=alice%40example.com&age=30"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(400, response.statusCode());
  }

  @Test
  void create_shouldReturn400_whenEmailIsInvalid() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users"))
            .POST(HttpRequest.BodyPublishers.ofString("name=Alice&email=notanemail&age=30"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(400, response.statusCode());
  }

  @Test
  void delete_shouldReturn200_whenUserExists() throws IOException, InterruptedException, SQLException {
    String id = service.createUser("Alice", "alice@example.com", 30).getId();

    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users/" + id))
            .DELETE()
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(200, response.statusCode());
  }

  @Test
  void delete_shouldReturn404_whenUserDoesNotExist() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users/nonexistent"))
            .DELETE()
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(404, response.statusCode());
  }

  @Test
  void update_shouldReturn200_whenUserExists() throws IOException, InterruptedException, SQLException {
    String id = service.createUser("Alice", "alice@example.com", 30).getId();

    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users/" + id))
            .PUT(HttpRequest.BodyPublishers.ofString("name=Bob&email=bob%40example.com&age=25"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("\"name\":\"Bob\""));
  }

  @Test
  void update_shouldReturn404_whenUserDoesNotExist() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users/nonexistent"))
            .PUT(HttpRequest.BodyPublishers.ofString("name=Bob&email=bob%40example.com&age=25"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(404, response.statusCode());
  }

  @Test
  void update_shouldReturn400_whenNameIsMissing() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users/any-id"))
            .PUT(HttpRequest.BodyPublishers.ofString("email=bob%40example.com&age=25"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(400, response.statusCode());
  }

  @Test
  void unknownMethod_shouldReturn405() throws IOException, InterruptedException {
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/api/users"))
            .method("PATCH", HttpRequest.BodyPublishers.noBody())
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(405, response.statusCode());
  }

  // --- SQLException → 500 ---

  private String startServerWith(UserService svc) throws IOException {
    HttpServer s = HttpServer.create(new InetSocketAddress(0), 0);
    s.createContext("/api/users", new UserHandler(svc));
    s.start();
    serversToStop.add(s);
    return "http://localhost:" + s.getAddress().getPort();
  }

  @Test
  void getAll_shouldReturn500_onSQLException() throws IOException, InterruptedException, SQLException {
    UserService failing = mock(UserService.class);
    when(failing.findAllUsers()).thenThrow(new SQLException("db error"));

    String url = startServerWith(failing);
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder().uri(URI.create(url + "/api/users")).GET().build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(500, response.statusCode());
  }

  @Test
  void create_shouldReturn500_onSQLException() throws IOException, InterruptedException, SQLException {
    UserService failing = mock(UserService.class);
    when(failing.createUser(any(), any(), anyInt())).thenThrow(new SQLException("db error"));

    String url = startServerWith(failing);
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/users"))
            .POST(HttpRequest.BodyPublishers.ofString("name=Alice&email=alice%40example.com&age=30"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(500, response.statusCode());
  }

  @Test
  void delete_shouldReturn500_onSQLException() throws IOException, InterruptedException, SQLException {
    UserService failing = mock(UserService.class);
    when(failing.deleteUser(any())).thenThrow(new SQLException("db error"));

    String url = startServerWith(failing);
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/users/some-id"))
            .DELETE()
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(500, response.statusCode());
  }

  @Test
  void update_shouldReturn500_onSQLException() throws IOException, InterruptedException, SQLException {
    UserService failing = mock(UserService.class);
    when(failing.updateUser(any(), any(), any(), anyInt())).thenThrow(new SQLException("db error"));

    String url = startServerWith(failing);
    HttpResponse<String> response = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create(url + "/api/users/some-id"))
            .PUT(HttpRequest.BodyPublishers.ofString("name=Alice&email=alice%40example.com&age=30"))
            .build(),
        HttpResponse.BodyHandlers.ofString()
    );

    assertEquals(500, response.statusCode());
  }
}
