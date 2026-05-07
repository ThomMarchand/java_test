# CLAUDE.md

## Project overview

A Java 21 CRUD REST API for user management, built with zero external web frameworks. Uses the JDK's built-in `com.sun.net.httpserver.HttpServer`, SQLite for persistence, and JTE for server-side rendered pages. Learning/lab project — deliberately minimal dependencies.

## Stack

| Layer | Technology |
|---|---|
| HTTP server | `com.sun.net.httpserver.HttpServer` (JDK built-in) |
| Persistence | SQLite via `sqlite-jdbc` 3.45.1 |
| Templating (SSR) | JTE (`gg.jte`) 3.1.12 |
| Logging | Logback + custom `Logger` util |
| Tests | JUnit Jupiter 5 + Mockito 5 |
| Build | Maven 3, Java 21 |

No Spring, no Quarkus, no Jackson, no Gson. Keep it that way unless asked.

## Architecture

```
Main.java               — manual wiring of the dependency graph
handler/
  api/UserHandler        — REST API (/api/users)
  template/              — SSR page handlers (/landing)
  StaticFileHandler      — serves static/ directory
service/UserService      — validation + business logic
repository/
  Repository<T>          — CRUD interface
  AbstractRepository<T>  — generic JDBC implementation
  UserRepository         — concrete User persistence
  RowMapper<T>           — maps ResultSet rows to entities
model/User               — UUID-based id, two constructors (new vs from DB)
exception/
  ValidationException    — thrown on bad input → HTTP 400
  NotFoundException      — thrown on missing resource → HTTP 404
renderer/TemplateRenderer — JTE wrapper (dev vs precompiled mode)
```

Dependency injection is manual. `Main.java` is the composition root — wire new dependencies there.

`UserService` takes `UserRepository` (concrete class, not the `Repository<T>` interface). `FakeUserRepository` extends `UserRepository` with `super(null)` to allow in-memory testing.

## Build and run

```bash
# Build fat JAR
mvn package

# Run tests
mvn test

# Run directly (from project root)
java -cp target/crud-user-1.0-SNAPSHOT-jar-with-dependencies.jar com.app.Main
```

Server starts on **port 8081**. Database file `crud.sqlite` is created in the working directory on first startup.

**Must run from project root** — `StaticFileHandler` resolves `static/` and `TemplateRenderer` resolves `src/main/jte/` relative to the working directory.

## JTE templates

Toutes les pages SSR sont rendues via JTE. La page statique `landing` (servie par
`StaticFileHandler`) est amenée à disparaître ; à terme, tout le rendu HTML passe
par des handlers JTE dans `handler/template/`.

### Structure du dossier `src/main/jte/`

```
src/main/jte/
  layout.jte        — shell HTML complet (<!DOCTYPE>, <head>, <nav>, <main>)
                      accepte `String title` + `Content content` (slot JTE)
  styles.jte        — point d'entrée CSS : importe reset.jte puis global.jte
  styles/
    reset.jte       — CSS reset
    global.jte      — variables et styles du design system
  pages/
    users.jte       — page liste des utilisateurs ; reçoit List<User>,
                      délègue le shell à @template.layout(…)
```

Chaque page dans `pages/` reçoit ses données Java en paramètre `@param` et compose
avec `@template.layout(title = "…", content = @\`…\`)` pour injecter son contenu
dans le slot.

`styles.jte` est inclus directement dans `layout.jte` pour centraliser les imports
CSS — ne pas dupliquer les `@template.styles.*` dans les pages individuelles.

Le plugin Maven `jte-maven-plugin` pré-compile les templates au moment de
`generate-sources`. Après toute modification d'un `.jte`, relancer
`mvn generate-sources` (ou `mvn compile`) avant de démarrer l'application.

`TemplateRenderer` auto-détecte le mode : `DirectoryCodeResolver` (rechargement à
chaud) quand `src/main/jte/` existe, sinon `TemplateEngine.createPrecompiled` dans
le JAR packagé.

## API

All request bodies are `application/x-www-form-urlencoded`. JSON responses are hand-built strings — no serialization library.

| Method | Path | Body params | Success |
|---|---|---|---|
| GET | `/api/users` | — | 200 JSON array |
| POST | `/api/users` | `name`, `email`, `age` | 201 |
| PUT | `/api/users/{id}` | `name`, `email`, `age` | 200 |
| DELETE | `/api/users/{id}` | — | 200 |

Exception-to-status mapping: `ValidationException` → 400, `NotFoundException` → 404, `SQLException` → 500.

## Testing strategy

Three layers, each in its own class:

- **Unit** (`UserServiceTest`): uses `FakeUserRepository` (in-memory, no DB).
- **Repository integration** (`UserRepositoryIntegrationTest`): real `UserRepository` against an in-memory SQLite connection (`jdbc:sqlite::memory:`). Schema is set up in `@BeforeEach`.
- **Handler integration** (`UserHandlerIntegrationTest`): real `HttpServer` on port 0, `FakeUserRepository` for happy-path tests, mocked `UserService` (Mockito) only for SQL-error scenarios. Spins up a fresh server per test.

Do not mock the repository in repository integration tests. Do not use a real SQLite file in tests.

## Conventions

**Commits:** Conventional Commits — `type(scope): message` (e.g. `feat(user): add pagination`, `fix(handler): handle missing age param`).

**Branches:** `feat/N-description`, `fix/description` off `develop`. Merge to `develop`, then `develop` → `main` for releases.

**JavaDoc:** required on all public methods and classes. Use the existing style (one-line `@param` / `@return`, no prose paragraphs).

**CI:** GitHub Actions runs `mvn test` and CodeQL on every PR to `develop` or `main`.
