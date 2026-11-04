# HttpIntegrationTest.java

## Simple overview
This test starts the real application and makes real HTTP requests to confirm that public pages load and protected routes reject unauthenticated users.

## Key behavior
- Starts the app on a random port with `@SpringBootTest`.
- Uses Java's `HttpClient` to issue real GET requests.
- Verifies the root page and `/api/config` are public.
- Verifies `/api/auth/me` returns 401 without a token.
- Uses the H2 test configuration; no external services are required.

## Deep dive

### Test setup
`@SpringBootTest(webEnvironment = RANDOM_PORT)` starts an embedded server on a random available port. `@LocalServerPort` injects that port, and the helper builds URLs such as:

```text
http://localhost:<port>/api/config
```

The test creates a standard `HttpClient` and sends GET requests with no authentication headers. This exercises the full server stack, including Spring Security filters, controllers, and static resource handling.

### What `loginPageAndConfigArePublic` checks
1. `GET /` returns 200 and the body contains `Online Marketplace`.
2. `GET /api/config` returns 200 and the body contains the test audience `https://mcplace-api`.
3. `GET /api/auth/me` returns 401 without a token.

These assertions verify that:

- The static welcome page is served at the root.
- The Auth0 configuration endpoint is publicly reachable.
- The protected endpoint is not accidentally public.

### Why use random ports
A random port avoids conflicts with a locally running instance or another test. The port is available only for the lifetime of the test context.

### Database behavior
The test uses `src/test/resources/application.properties`, so it runs against H2 in PostgreSQL mode. No external PostgreSQL service is required locally. CI overrides the datasource to PostgreSQL 16 and runs the same test, so the test must not depend on H2-only behavior.

### What it does not cover
- Successful authenticated requests, because that requires a valid Auth0 token.
- Browser-side JavaScript behavior.
- Detailed controller response fields; the MockMvc tests cover those.
- CORS, because all requests are same-origin.
