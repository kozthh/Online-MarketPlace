# application-dev.properties

## Simple overview
This profile lets the app run locally without installing PostgreSQL by using an in-memory H2 database instead.

## Key behavior
- Activated with `-Dspring-boot.run.profiles=dev`.
- Uses an in-memory H2 database named `mcplace`.
- Enables H2 PostgreSQL compatibility mode with lowercase identifiers.
- Keeps Flyway enabled and JPA set to `ddl-auto=validate`.

## Related files
- `application.properties`
- `db/migration/V1__create_users_and_user_roles.sql`

## Deep dive

### When this file is loaded
Spring Boot loads `application.properties` first, then loads `application-dev.properties` only when the `dev` profile is active.

Ways to activate it:

- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Set `SPRING_PROFILES_ACTIVE=dev` in the environment before starting the jar.

Without the profile, the default PostgreSQL configuration from `application.properties` is used.

### Datasource override
This profile overrides only the datasource properties:

```properties
spring.datasource.url=jdbc:h2:mem:mcplace;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
```

The URL components mean:

- `jdbc:h2:mem:mcplace`: create an in-memory database named `mcplace`.
- `MODE=PostgreSQL`: enable H2's PostgreSQL compatibility behavior so PostgreSQL-style SQL is more likely to work.
- `DATABASE_TO_LOWER=TRUE`: store unquoted identifiers in lowercase, matching PostgreSQL's usual behavior.
- `DB_CLOSE_DELAY=-1`: keep the database alive until the JVM exits, even if all connections close.

The username is `sa` and the password is empty, which is normal for a local H2 database.

### Schema and persistence
The profile does not disable Flyway or change `ddl-auto=validate`. On startup:

1. Flyway applies `V1__create_users_and_user_roles.sql`.
2. Hibernate validates the `User` mapping against the created tables.

All data is lost when the process stops. Every fresh start recreates the schema from the migration files. This is useful for development and tests, but it is not a substitute for PostgreSQL when checking production behavior.

### Compatibility warning
H2 in PostgreSQL mode is close to PostgreSQL but not identical. A migration or query that works here can still fail against PostgreSQL. Keep SQL portable and remember that CI also runs the tests against a real PostgreSQL 16 service.
