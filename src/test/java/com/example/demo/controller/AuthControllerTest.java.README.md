# AuthControllerTest.java

## Simple overview
This test confirms that the current-user endpoint cannot be called without logging in.

## Key behavior
- Loads the full context with `@SpringBootTest` and `@AutoConfigureMockMvc`.
- Calls `GET /api/auth/me` without a token.
- Expects HTTP 401, confirming the route is protected.

## Deep dive

### MockMvc setup
`@SpringBootTest` loads the application context, and `@AutoConfigureMockMvc` provides a `MockMvc` instance configured with the application's web and security layers.

`MockMvc` sends requests through the Spring MVC test infrastructure without starting a real network server. Security filters are still applied, so this test exercises the route protection behavior.

### The test request
The test performs:

```java
mockMvc.perform(get("/api/auth/me"))
       .andExpect(status().isUnauthorized());
```

No `Authorization` header is supplied. The request is rejected by `SecurityConfig` before it reaches `AuthController`, producing 401.

### What this proves
- The endpoint is not in the public route list.
- The security filter chain is active in the test context.
- The authentication entry point returns the expected status for anonymous requests.

### What it does not prove
- That a valid Auth0 token is accepted.
- That the controller returns the correct `sub`, `email`, and `scopes` values.
- That the frontend sends the token correctly.

Testing the success path would require mocking or generating a valid Auth0 authentication object, which this test intentionally avoids.
