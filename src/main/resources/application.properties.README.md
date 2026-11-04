# application.properties

## Simple overview
This is the main configuration file for the application. It defines the default database, JPA, Flyway, and Auth0 settings.

## Key behavior
- Sets the application name to `mcplace`.
- Uses PostgreSQL at `localhost:5432/mcplace` by default.
- Supports `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment overrides.
- Uses `ddl-auto=validate` with Flyway enabled and disables open-in-view.
- Supports `AUTH0_DOMAIN`, `AUTH0_AUDIENCE`, and `AUTH0_CLIENT_ID` overrides; committed defaults are for the development tenant.

## Related files
- `application-dev.properties`
- `../test/resources/application.properties`
- `controller/ConfigController.java`

## Deep dive

### Database configuration
The datasource properties use Spring's placeholder syntax with defaults:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/mcplace}
spring.datasource.username=${DB_USERNAME:}
spring.datasource.password=${DB_PASSWORD:}
```

This means:

- If `DB_URL`, `DB_USERNAME`, or `DB_PASSWORD` is set in the environment, that value is used.
- Otherwise, the URL falls back to a local PostgreSQL database named `mcplace`, and the username/password fall back to empty strings.
- The default profile therefore requires a running PostgreSQL instance unless the datasource is overridden.

CI does not set `DB_URL`; it sets the standard Spring variables `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`. Those environment properties override the datasource configuration for the PostgreSQL service container.

### JPA and Flyway
- `spring.jpa.hibernate.ddl-auto=validate` tells Hibernate to check the existing schema against the entity mappings. It does not create or alter tables. A mismatch fails startup.
- `spring.flyway.enabled=true` runs Flyway migrations before Hibernate validation. Migrations are read from the default `classpath:db/migration` location.
- `spring.jpa.open-in-view=false` closes the persistence context when the repository/controller call finishes. Lazy loading after that point will fail. `User.roles` is eager, so it is not affected, but future lazy relationships need a transaction boundary.

Together, these settings make Flyway the owner of the database schema.

### Auth0 configuration
The `auth0.*` properties define the tenant and API used by both the backend token filter and the `/api/config` endpoint:

- `auth0.domain` is the Auth0 tenant domain.
- `auth0.audience` is the API identifier expected in access tokens.
- `auth0.client-id` is the public SPA client ID used by the browser.

The environment overrides are `AUTH0_DOMAIN`, `AUTH0_AUDIENCE`, and `AUTH0_CLIENT_ID`. The committed defaults point to a development tenant and are not secrets, but production deployments should override them explicitly.

### Profile behavior
`application.properties` is always loaded. When the `dev` profile is active, `application-dev.properties` overrides the datasource properties with an H2 configuration. The test configuration under `src/test/resources/application.properties` is used by the test classpath instead.

### Production checklist
- Set the datasource environment variables.
- Set the Auth0 domain, audience, and client ID for the target tenant.
- Run Flyway migrations before starting a new version.
- Do not add secrets to this file.
