# index.html

## Simple overview
This is the visible login page. It loads the Auth0 browser SDK and the application script, then shows either the signed-out buttons or the signed-in user details.

## Key behavior
- Loads `styles.css` and the Auth0 SPA SDK from a CDN.
- Contains logged-out and logged-in sections plus a result area.
- Loads `app.js` directly; there is no frontend build step.
- The page itself is public, while protected API calls require a token.

## Related files
- `app.js`
- `styles.css`
- `config/SecurityConfig.java`

## Deep dive

### Document structure
- The page declares UTF-8, a responsive viewport, and the title `Online Marketplace`.
- `styles.css` is loaded from `/styles.css`.
- The main content is a `main.card` containing:
  - A status paragraph with ID `status`.
  - A signed-out section with IDs `logged-out`, `login`, `signup`, and `login-email`.
  - A signed-in section with IDs `logged-in`, `avatar`, `user-name`, `user-email`, `call-api`, and `logout`.
  - A `pre` element with ID `result` for API output.

Every ID used here is referenced by `app.js`. Changing one without updating the script breaks the page behavior.

### Script loading order
The page loads two scripts at the end of the body:

1. `https://cdn.auth0.com/js/auth0-spa-js/2.30.0/auth0-spa-js.production.js`
2. `/app.js`

Order matters. `app.js` reads `window.auth0.createAuth0Client` during initialization, so the Auth0 SDK must load first. The scripts are at the end of the body so the DOM elements already exist when `app.js` runs.

The Auth0 SDK is loaded from a public CDN. There is no local copy, package manager, bundler, or build step. If the CDN is unavailable, the page cannot initialize Auth0.

### Public access
`index.html` is served by Spring Boot's static resource handling. `SecurityConfig` permits `/`, `/index.html`, `/app.js`, `/styles.css`, and `/favicon.ico`, so all assets needed before login can load.

The root path `/` is the typical entry point. After Auth0 redirects back, the browser returns to `window.location.origin`, which resolves to the root and serves this page again.

### What the page does not contain
- No inline authentication logic; that is in `app.js`.
- No form posts or server-rendered data.
- No CSRF token, because authentication is Bearer-token based.
- No marketplace features yet; it is currently a login/demo shell.
