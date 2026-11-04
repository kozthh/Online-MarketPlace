# GlobalExceptionHandler.java

## Simple overview
This class turns certain application exceptions into a consistent JSON error response instead of letting the default Spring error page handle them.

## Key behavior
- Handles `MethodArgumentNotValidException` as HTTP 400.
- Joins field validation messages into `field: message` form.
- Wraps responses in `ApiError` with the request URI.
- Other exception types are left to Spring's default handling.

## Related files
- `exception/ApiError.java`

## Deep dive

### Advice scope
`@RestControllerAdvice` applies to all REST controllers in the application. When a controller throws an exception, Spring checks this class for a matching `@ExceptionHandler` method.

The only handler currently defined is for `MethodArgumentNotValidException`. Spring raises that exception when a request body annotated with `@Valid` fails bean validation. There are no validated request DTOs in the project yet, so this handler is preparation for future endpoints rather than an actively used path.

### Validation message format
The handler reads `exception.getBindingResult().getFieldErrors()` and maps each field error to:

```text
fieldName: validation message
```

All field errors are joined with `, `. For example, two invalid fields could produce:

```text
email: must not be blank, firstName: size must be between 1 and 50
```

Only field errors are included. Object-level/global errors and the rejected values are not included.

### Response construction
The private `build` method creates a `ResponseEntity<ApiError>`:

- Status comes from the supplied `HttpStatus`, currently only `BAD_REQUEST`.
- Timestamp is `Instant.now()`.
- `error` is the status reason phrase.
- `message` is the constructed validation text.
- `path` is `request.getRequestURI()`.

### What is not handled
- Generic `RuntimeException` or database exceptions are not converted to `ApiError`; Spring Boot's default error handling applies.
- Authentication and authorization failures are handled by Spring Security before a controller is reached, so they do not pass through this advice.
- The handler does not log the exception.

### Adding handlers
For consistent API errors, add more `@ExceptionHandler` methods here and reuse `build`. Avoid exposing internal exception messages or stack details to clients.
