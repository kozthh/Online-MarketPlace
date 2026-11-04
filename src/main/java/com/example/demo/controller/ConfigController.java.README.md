# ConfigController.java

## Simple overview
This controller gives the browser the Auth0 settings it needs to start the login flow. The endpoint is intentionally public.

## Key behavior
- `GET /api/config` is public.
- Reads `auth0.domain`, `auth0.client-id`, and `auth0.audience` from configuration.
- Returns those three values as JSON for the Auth0 SPA client.

## Related files
- `src/main/resources/application.properties`
- `src/main/resources/static/app.js`
- `config/SecurityConfig.java`

## Deep dive

### Constructor injection
The controller stores three values injected with `@Value`:

- `${auth0.domain}` becomes the Auth0 tenant domain.
- `${auth0.client-id}` becomes the SPA application client ID.
- `${auth0.audience}` becomes the API identifier/audience used when requesting tokens.

These values are resolved when the Spring bean is created. They come from `application.properties` and can be overridden with environment variables such as `AUTH0_DOMAIN`, `AUTH0_CLIENT_ID`, and `AUTH0_AUDIENCE`.

Because the values are read once at startup, changing an environment variable requires restarting the application.

### Response shape
The method returns `Map.of(...)` with these JSON keys:

- `domain`
- `clientId`
- `audience`

The Java property name `auth0.client-id` is not the same as the JSON key `clientId`; `app.js` expects the camel-case key.

`Map.of` returns an immutable map. The response field order is not something callers should depend on.

### Why this route is public
The static frontend has to call `/api/config` before the user logs in, because `createAuth0Client` needs the domain, client ID, and audience first. That is why `SecurityConfig` includes `/api/config` in the `permitAll` list.

These three values are public client configuration, not secrets. Do not add a client secret or any private key to this response.

### Relationship to Auth0 configuration
The same `auth0.*` properties are also used by the `auth0-springboot-api` library to validate incoming tokens. The domain and audience must describe the same Auth0 tenant and API that the frontend uses. If they diverge, the browser can obtain a token that the backend rejects.

### Tests
`ConfigControllerTest` asserts the exact test-profile values for all three fields. If the test configuration changes, those assertions must change too.
