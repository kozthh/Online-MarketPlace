# app.js

## Simple overview
This script runs the login page. It configures Auth0, shows whether the user is signed in, and calls the protected backend endpoint with the user's token.

## Key behavior
- Fetches `/api/config` and initializes the Auth0 SPA client.
- Uses the current origin as the redirect URI with the `openid profile email` scope.
- Handles the Auth0 redirect callback and cleans the URL.
- Renders logged-in and logged-out states, including user profile details.
- Buttons start login, signup, email-code login, and logout flows.
- Calls `/api/auth/me` with a silently obtained Bearer token.

## Related files
- `index.html`
- `controller/ConfigController.java`
- `controller/AuthController.java`

## Deep dive

### DOM dependencies
At the top of the file, the script looks up elements by ID:

- `status`, `logged-out`, `logged-in`, `login`, `signup`, `login-email`
- `logout`, `call-api`, `user-name`, `user-email`, `avatar`, `result`

These IDs must exist in `index.html`. If an ID is renamed or removed, the corresponding variable becomes `null` and later property access fails.

### Initialization flow
`init()` performs the startup sequence:

1. Reads `window.auth0.createAuth0Client`, which is provided by the Auth0 SDK loaded before this script.
2. Throws an error if the SDK is missing.
3. Fetches `/api/config` and fails if the response is not OK.
4. Creates the Auth0 client with:
   - `domain` and `clientId` from the backend configuration.
   - `redirect_uri: window.location.origin`.
   - `audience` from the backend configuration.
   - `scope: 'openid profile email'`.
5. Checks whether the URL contains both a `state` parameter and either `code` or `error`.
6. If so, calls `handleRedirectCallback()` and removes the query string with `history.replaceState`.
7. Calls `render()`.

The redirect URI must be registered as an allowed callback URL in the Auth0 application. The audience must match the API identifier used by the backend.

### Rendering state
`render()` calls `auth0Client.isAuthenticated()` and toggles the `hidden` property on the logged-out and logged-in sections.

When authenticated, it calls `auth0Client.getUser()` and displays:

- `user.name`, falling back to `user.nickname`.
- `user.email`.
- `user.picture` as the avatar image.

When not authenticated, it hides and clears the API result area.

### Calling the protected API
`callProtectedApi()`:

1. Calls `auth0Client.getTokenSilently()` to get an access token for the configured audience.
2. Sends `GET /api/auth/me` with `Authorization: Bearer <token>`.
3. Shows the JSON response in the `result` element.

The function always tries to parse the response as JSON. A 401 from the backend is expected when the token is missing, expired, or intended for a different audience; the UI displays the parsed error or the thrown error message.

Silent token acquisition can fail if Auth0 requires interaction, if the browser blocks third-party storage, or if consent is missing. The button handler catches that failure and displays the error text.

### Button wiring
- Log in: `loginWithRedirect()`.
- Sign up: `loginWithRedirect({ authorizationParams: { screen_hint: 'signup' } })`.
- Email code: `loginWithRedirect({ authorizationParams: { connection: 'email' } })`.
- Log out: `logout({ logoutParams: { returnTo: window.location.origin } })`.
- Call API: wraps `callProtectedApi()` and displays failures.

The `auth0Client` variable is kept in module/script scope. This file is loaded as a classic browser script, not an ES module, and there is no bundler or npm step.

### Same-origin behavior
Because the page and API are served by the same Spring Boot application, the browser requests are same-origin. There is no CORS configuration in this project for these calls.
