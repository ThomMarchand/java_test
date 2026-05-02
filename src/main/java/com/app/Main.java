package com.app;

import java.io.IOException;
import java.sql.SQLException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import com.app.handler.StaticFileHandler;
import com.app.handler.api.UserHandler;
import com.app.handler.template.LandingTemplateHandler;
import com.app.renderer.TemplateRenderer;
import com.app.repository.UserRepository;
import com.app.service.UserService;
import com.app.util.Logger;

/**
 * Application entry point.
 *
 * <p>Wires up the dependency graph, registers HTTP contexts, and starts the server.</p>
 */
public class Main {

  /**
   * Starts the HTTP server on port {@code 8081}.
   *
   * @param args command-line arguments (unused)
   * @throws IOException  if the server socket cannot be opened
   * @throws SQLException if the database connection or schema initialisation fails
   */
  public static void main(String[] args) throws IOException, SQLException {
    int port = 8081;
    TemplateRenderer renderer = new TemplateRenderer();
    UserRepository repository = new UserRepository(Database.getConnection());
    UserService service = new UserService(repository);


    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/api/users", new UserHandler(service));

    server.createContext("/", new StaticFileHandler());
    server.createContext("/landing", new LandingTemplateHandler(service, renderer));

    server.start();

    Logger.printLn("Server running on : http://localhost:" + port);
    Logger.printLn("Database : crud.sqlite");
  }
}