# src/test/resources/application.properties

## Simple overview
This is the configuration used only when tests run. It keeps tests independent of a local PostgreSQL installation by using H2.

## Key behavior
- Uses an in-memory H2 database named `mcplace`.
- Enables H2 PostgreSQL compatibility mode with lowercase identifiers.
- Keeps Flyway enabled and JPA set to `ddl-auto=validate`.
- Defines fixed Auth0 test values used by configuration tests.
- CI overrides the datasource with PostgreSQL 16, so tests must remain compatible with both databases.

## Deep dive

### Test classpath behavior
This file lives in `src/test/resources`, so it is used for the test runtime and takes precedence over the main `src/main/resources/application.properties` for the same property names.

It defines:

```properties
spring.datasource.url=jdbc:h2:mem:mcplace;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
```

The URL components:

- `jdbc:h2:mem:mcplace`: in-memory database named `mcplace`.
- `MODE=PostgreSQL`: PostgreSQL-compatible behavior.
- `DATABASE_TO_LOWER=TRUE`: lowercase unquoted identifiers.
- `DB_CLOSE_DELAY=-1`: keep the database alive until the JVM exits.

`sa` with an empty password is the normal local H2 login. No external database is needed for a normal local test run.

### Schema validation
- `spring.flyway.enabled=true` runs the same migrations as production.
- `spring.jpa.hibernate.ddl-auto=validate` verifies that the entities match the migrated schema.

This means tests fail if an entity change is made without a corresponding migration, which mirrors production behavior.

### Auth0 test values
The test properties set fixed values for:

- `auth0.domain`
- `auth0.audience`
- `auth0.client-id`

`ConfigControllerTest` asserts these exact values, and `HttpIntegrationTest` checks for the audience string in `/api/config`. Keep the test values stable or update the assertions together.

### CI with PostgreSQL
CI starts a PostgreSQL 16 service and sets:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Those environment properties override the H2 datasource for the test run. The test properties still provide the Auth0 values. Because the same tests run against both databases, avoid H2-specific SQL and avoid assumptions about database-specific ordering or types.

### Shared in-memory database
`DB_CLOSE_DELAY=-1` keeps the in-memory database alive for the JVM, even after connections close. Multiple test classes in the same run can therefore share the same `mcplace` database. The current tests do not write data, but future tests that modify data should clean up or isolate their state.
