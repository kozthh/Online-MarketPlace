# ConfigControllerTest.java

## Simple overview
This test verifies that the browser can load the Auth0 configuration and the login page without being logged in.

## Key behavior
- Loads the full context with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- Verifies `/api/config` is accessible without authentication and returns the expected test Auth0 values.
- Verifies `/index.html` is public and contains the page heading.

## Deep dive

### `exposesAuth0ConfigWithoutAuthentication`
The test sends an unauthenticated GET to `/api/config` and asserts:

- HTTP status 200.
- `$.domain` equals `dev-10vbzn05euvzgpmi.us.auth0.com`.
- `$.audience` equals `https://mcplace-api`.
- `$.clientId` equals `9GfoU7nayQRGT3VYZUpZvZB69LovAmAT`.

These values come from `src/test/resources/application.properties`. The test verifies both the public route rule in `SecurityConfig` and the JSON key names produced by `ConfigController`.

If the test Auth0 configuration changes, these assertions must be updated. If only the production configuration changes, these assertions should still pass because tests use their own properties.

### `servesLoginPageWithoutAuthentication`
The test sends an unauthenticated GET to `/index.html` and asserts:

- HTTP status 200.
- The response body contains `Online Marketplace`.

This confirms that the static welcome page is served and is publicly accessible. It does not execute `app.js` or test the Auth0 browser flow.

### Why MockMvc
`MockMvc` exercises Spring MVC routing, controller serialization, static resource handling, and security filters without opening a network port. It is faster than a full HTTP client test and is sufficient for checking routes and response values.

### What it does not cover
- The real root path `/`; `HttpIntegrationTest` covers that.
- JavaScript behavior, Auth0 redirects, or token acquisition.
- Protected API behavior beyond the route list.
- Content of `styles.css` or `app.js`.
