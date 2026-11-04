# McplaceApplication.java

## Simple overview
This is the starting point of the Spring Boot application. Running this class starts the whole marketplace backend.

## Key behavior
- `@SpringBootApplication` enables component scanning, auto-configuration, and configuration support.
- `main` delegates to `SpringApplication.run`.
- Startup runs Flyway migrations and JPA schema validation.

## Related files
- `src/main/resources/application.properties`
- `src/main/resources/db/migration/`

## Deep dive

### What `@SpringBootApplication` does here
`@SpringBootApplication` combines three annotations:

- `@SpringBootConfiguration`: marks this as the main configuration class.
- `@EnableAutoConfiguration`: lets Spring Boot configure the web server, datasource, JPA, Flyway, security, and Actuator based on the dependencies on the classpath.
- `@ComponentScan`: scans `com.example.demo` and every subpackage, which is how the controllers, `SecurityConfig`, `UserRepository`, and exception handler are discovered.

Because the class is in `com.example.demo`, any new bean placed in that package or below is picked up automatically. A class outside that package would need explicit configuration or a wider scan.

### Startup flow
1. `main` calls `SpringApplication.run(McplaceApplication.class, args)`.
2. Spring Boot loads `application.properties`.
3. If the `dev` profile is active, `application-dev.properties` overrides matching datasource properties.
4. The datasource is created from the configured URL, username, and password.
5. Flyway applies migrations from `classpath:db/migration`.
6. Hibernate starts with `ddl-auto=validate`, so each entity must match the migrated schema. A mismatch stops startup.
7. The embedded server starts and begins serving the static page and API routes.

There are no custom initializers, runners, or bean definitions in this class. Startup behavior comes from configuration and auto-configuration.

### How this affects changes
- Adding a new controller, repository, or configuration class under `com.example.demo` needs no registration in this file.
- A schema change needs a new Flyway migration, not just an entity edit, because Hibernate will validate and refuse to start on a mismatch.
- Tests load the same application class. `McplaceApplicationTests` exists specifically to catch startup problems such as bad configuration or an invalid schema.
