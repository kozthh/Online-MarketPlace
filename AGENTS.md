# AGENTS.md

## Project

Spring Boot 4.1.2 / Java 21 marketplace backend plus a plain static Auth0 login page. Root package is `com.example.demo`; app name is `mcplace`. There is no npm/frontend build.

## Commands

- Full build + tests (what CI runs): `./mvnw verify`
- All tests: `./mvnw test`
- Single test class: `./mvnw -Dtest=AuthControllerTest test`
- Single test method: `./mvnw -Dtest=AuthControllerTest#meWithoutTokenReturnsUnauthorized test`
- Run with in-memory H2: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- Run default profile (needs PostgreSQL at `localhost:5432/mcplace`, plus `DB_USERNAME`/`DB_PASSWORD`): `./mvnw spring-boot:run`
- Docker image: `docker build -t online-marketplace .` (multi-stage Java 21, runs as non-root, port 8080)

No lint/formatter/checkstyle command is configured. Qodana runs only in CI/IDE.

## Architecture and config

- Auth is Auth0 JWT via `auth0-springboot-api`. `config/SecurityConfig.java` is the source of truth for public routes: static assets, `/api/config`, `/actuator/health`, `/actuator/info`; everything else requires a Bearer token. Update it when adding public endpoints.
- `/api/config` exposes Auth0 domain/clientId/audience to the static frontend. `/api/auth/me` is protected and casts `Authentication` to `Auth0AuthenticationToken`.
- Flyway owns the schema (`src/main/resources/db/migration`); JPA is `ddl-auto=validate`. Add a new `V2__...sql` migration for schema changes; never edit an applied migration.
- Default profile uses PostgreSQL; dev/test use H2 in PostgreSQL compatibility mode (`MODE=PostgreSQL`). Tests use `src/test/resources/application.properties`. CI reruns the same tests against PostgreSQL 16, so keep entities and migrations valid for both.
- Frontend lives in `src/main/resources/static` as plain HTML/CSS/JS. Auth0 SPA SDK is loaded from a CDN in `index.html`; edit files directly, no build step.

## CI/CD

- `.github/workflows/ci.yml`: `mvn -B verify` on pushes/PRs to `main`, with a temporary PostgreSQL 16 service.
- `.github/workflows/cd.yml`: after successful CI on `main`, builds and pushes a `ghcr.io` image; the deploy job is intentionally disabled.
- `.github/workflows/qodana_code_quality.yml`: Qodana JVM scan on PRs/pushes.

## Gotchas

- This workspace currently shows nearly every file as modified due to CRLF-vs-LF line endings. Do not stage or commit whitespace-only diffs. Keep `mvnw` LF and `*.cmd` CRLF per `.gitattributes`.
- Auth0 tenant defaults are committed in `application*.properties`. Prefer environment overrides (`AUTH0_DOMAIN`, `AUTH0_AUDIENCE`, `AUTH0_CLIENT_ID`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) and do not add secrets.
- Windows `mvnw` can fail with CRLF; the Dockerfile explicitly strips CRLF before running it.
