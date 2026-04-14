package com.app;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import com.app.handler.StaticFileHandler;
import com.app.handler.UserHandler;
import com.app.repository.UserRepository;
import com.app.service.UserService;
import com.app.util.Logger;

public class Main {
  public static void main(String[] args) throws IOException {
    int port = 8081;
    UserRepository repository = new UserRepository();
    UserService service = new UserService(repository);

    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    server.createContext("/api/users", new UserHandler(service));
    server.createContext("/", new StaticFileHandler());

    server.start();

    Logger.printLn("Server running on : http://localhost:" + port);
  }
}