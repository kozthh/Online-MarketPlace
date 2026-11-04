# SecurityConfig.java

## Simple overview
This file decides which pages and API routes are public and which ones require an Auth0 login token.

## Key behavior
- Permits `/`, static assets, `/api/config`, `/actuator/health`, and `/actuator/info`.
- Requires authentication for every other request.
- Registers `Auth0AuthenticationFilter` before the username/password filter.
- Disables CSRF, uses stateless sessions, and returns 401 for unauthenticated requests.
- Add new public endpoints to the `permitAll` list here.

## Related files
- `controller/AuthController.java`
- `controller/ConfigController.java`
- `src/main/resources/application.properties`

## Deep dive

### The filter chain
`securityFilterChain` builds one Spring Security filter chain:

- `csrf(csrf -> csrf.disable())` turns off CSRF protection because the API is token-based and does not use browser cookies for authentication.
- `sessionCreationPolicy(STATELESS)` prevents Spring Security from creating or using an HTTP session. Each request must authenticate itself with a token.
- `authorizeHttpRequests` defines the public route list and protects everything else.
- `exceptionHandling(...)` installs an authentication entry point that calls `response.sendError(401)`. This produces a plain 401 response rather than a custom JSON body.
- `addFilterBefore(auth0Filter, UsernamePasswordAuthenticationFilter.class)` places the Auth0 filter early in the chain so it can validate a Bearer token and populate the security context.

There is no form login, HTTP Basic, or custom login page configured. Authentication comes from the Auth0 filter and the `auth0-springboot-api` library.

### Public routes and matching
The public list is exact path matching, not a broad wildcard:

- `/` serves the welcome page, which resolves to the static `index.html`.
- `/index.html`, `/app.js`, `/styles.css`, and `/favicon.ico` allow the login page assets to load before a user signs in.
- `/api/config` must be public because the frontend needs the Auth0 configuration before it can authenticate.
- `/actuator/health` and `/actuator/info` are public operational endpoints.

`anyRequest().authenticated()` protects future routes by default. If you add another public endpoint, such as an image directory or a registration endpoint, it must be added here or it will return 401.

### What happens on an unauthenticated request
For a protected route such as `/api/auth/me`, a request without a valid Bearer token reaches the authorization step, fails, and is handled by the custom entry point. The response status is 401. The request does not reach the controller.

The Auth0 filter runs before the username/password filter, but it does not replace Spring Security authorization. The filter validates the token; this configuration still decides whether the route requires authentication.

### Important implications
- The app is stateless. Do not expect a server-side session after login.
- Token validation is delegated to the Auth0 library and the `auth0.*` properties.
- The `permitAll` list is the single source of truth for public routes. Changing a route's security without changing this file is usually a mistake.
- Role-based authorization is not configured yet. The app currently checks that a user is authenticated, not that they have a specific role.
- `AuthController` casts the authenticated principal to `Auth0AuthenticationToken`, so changing the authentication mechanism here can break that controller.
