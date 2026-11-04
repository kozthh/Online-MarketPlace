# McplaceApplicationTests.java

## Simple overview
This is a smoke test that simply checks whether the application can start.

## Key behavior
- Uses `@SpringBootTest` to load the full context.
- Exercises Flyway startup and JPA schema validation.
- Relies on `src/test/resources/application.properties` for the H2 datasource.

## Deep dive

### What `@SpringBootTest` loads
The test starts the Spring application context using the real `McplaceApplication` configuration and the test classpath.

It does not start a real HTTP server because no `webEnvironment` is specified; the default is a mock web environment. The test only needs the context to initialize.

### What a successful run proves
The empty `contextLoads` test will fail if any of these are broken:

- Spring cannot create a required bean.
- The datasource configuration is invalid.
- Flyway cannot apply migrations.
- Hibernate cannot validate the entity mappings against the migrated schema.
- An Auth0 configuration property is missing in a way that prevents bean creation.

This is why the test is valuable despite having no assertions: startup itself is the assertion.

### Database
The test uses the H2 configuration from `src/test/resources/application.properties`. Flyway creates the schema from `V1__create_users_and_user_roles.sql`, and Hibernate validates it. If an entity field and the migration disagree, this test fails early.

### What it does not cover
- HTTP routes and status codes.
- Security behavior.
- Business logic.
- Real PostgreSQL behavior.

Use `HttpIntegrationTest`, `AuthControllerTest`, and `ConfigControllerTest` for route-level checks.
