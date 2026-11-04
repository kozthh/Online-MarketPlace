# styles.css

## Simple overview
This stylesheet controls the appearance of the login page, including its centered card, buttons, profile image, and dark-mode colors.

## Key behavior
- Centers a card layout and styles headings, muted text, buttons, avatar, and API result output.
- Defines primary and secondary button appearances.
- Supports dark mode through `prefers-color-scheme`.
- Plain CSS with no preprocessor or build step.

## Related files
- `index.html`

## Deep dive

### Global styling
- `:root` declares `color-scheme: light dark`, allowing the browser to render native controls appropriately, and sets a system font stack.
- `body` removes the default margin, uses a full viewport height, centers content with CSS grid, and sets the light-mode background and text colors.

### Card layout
`.card` uses:

- `width: min(420px, 90vw)` so the card stays narrow but never exceeds the screen.
- Padding, rounded corners, white background, and a soft box shadow.

This is the main visual container defined in `index.html` as `<main class="card">`.

### Text and controls
- `h1` removes the top margin and sets the page title size.
- `.muted` uses gray text for secondary information such as status and email.
- `button` removes the border, adds padding, rounded corners, pointer cursor, and spacing.
- `button.primary` is blue with white text.
- `button.secondary` is light gray with dark text.

### Profile and result output
- `#avatar` is displayed as a block and rounded into a circle.
- `pre` displays API JSON with a dark background, light text, horizontal scrolling, and a smaller font.

### Dark mode
The `@media (prefers-color-scheme: dark)` block changes:

- Body background and text colors.
- Card background.
- Muted text color.
- Secondary button colors.

Primary buttons remain blue.

### Hidden elements
The HTML uses the `hidden` attribute for sections that JavaScript toggles. Note that the `#avatar` rule sets `display: block`. In the browser cascade, an author stylesheet rule can override the user-agent style used by `[hidden]`, so if the avatar must be reliably hidden when there is no picture, prefer a CSS class-based rule instead of relying only on the attribute.

### No build tooling
This is plain CSS served directly from `src/main/resources/static`. There is no Sass, PostCSS, minification, or generated output. Edit the file directly.
