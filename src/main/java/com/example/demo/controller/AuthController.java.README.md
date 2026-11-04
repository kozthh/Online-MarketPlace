# AuthController.java

## Simple overview
This controller returns information about the currently signed-in user. It can only be called with a valid Auth0 access token.

## Key behavior
- `GET /api/auth/me` requires a valid Auth0 Bearer token.
- Casts the Spring `Authentication` object to `Auth0AuthenticationToken`.
- Returns the token subject, email claim, and scopes as JSON.

## Related files
- `config/SecurityConfig.java`
- `src/main/resources/static/app.js`

## Deep dive

### Routing and response
- `@RestController` means every method returns data serialized to JSON.
- `@RequestMapping("/api/auth")` sets the base path.
- `@GetMapping("/me")` maps the full route `GET /api/auth/me`.

The endpoint is protected because it is not in the public route list in `SecurityConfig`.

### Method parameters
`me(Authentication authentication)` uses Spring Security's argument resolution. For an authenticated request, the object is expected to be an `Auth0AuthenticationToken` created by the Auth0 filter.

The cast is intentional but fragile:

- If the route is made public, the principal may be an anonymous authentication token, and the cast will fail.
- If the authentication filter or library is changed, the token type may change.
- The controller assumes the token has already been validated by the security filter chain.

### Returned fields
The response contains:

- `sub`: `authentication.getName()`, which is the Auth0 subject identifier.
- `email`: `token.getClaim("email")`, read directly from the token claims. It can be null if the token does not include the email claim or the requested scope did not grant it.
- `scopes`: the scopes attached to the token, returned as-is by the Auth0 token object.

The method builds a `HashMap`, so the response field order is not guaranteed.

### What this endpoint does not do
- It does not read from or write to the `User` table.
- It does not create a local user record on first login.
- It does not check the `Role` enum or database roles.
- It does not verify the email claim exists.

It is a diagnostic/identity endpoint that reflects the validated Auth0 token. If the application later needs local user synchronization, that logic should be added here or, more cleanly, in a service called from here.

### Frontend connection
`app.js` obtains an access token with `getTokenSilently()` and sends it as `Authorization: Bearer <token>` to this endpoint. A successful call displays the JSON response in the page result area. A missing or invalid token produces 401 from the security layer.
