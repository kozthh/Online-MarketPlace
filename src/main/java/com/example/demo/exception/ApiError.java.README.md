# ApiError.java

## Simple overview
This record defines the standard shape of an error returned by the API.

## Key behavior
- Record fields: `timestamp`, `status`, `error`, `message`, and `path`.
- Constructed by `GlobalExceptionHandler`; not used directly by controllers.

## Related files
- `exception/GlobalExceptionHandler.java`

## Deep dive

### Record semantics
`ApiError` is a Java record, so it is immutable and automatically provides:

- A canonical constructor taking all five fields.
- Accessor methods named `timestamp()`, `status()`, `error()`, `message()`, and `path()`.
- `equals`, `hashCode`, and `toString` implementations.

Jackson serializes the record as a JSON object with the same field names.

### Field meanings
- `timestamp`: when the error was created. The handler passes `Instant.now()`, which is serialized as an ISO-8601 timestamp.
- `status`: the numeric HTTP status code, such as `400`.
- `error`: the HTTP reason phrase, such as `Bad Request`.
- `message`: a human-readable detail, such as joined validation messages.
- `path`: the request URI that produced the error.

### Current usage
Only `GlobalExceptionHandler` creates this object. Controllers do not return it directly. Security failures such as a missing Bearer token are handled by `SecurityConfig` with a plain 401 response and do not produce an `ApiError` body.

### Extending it
If you add more error details, such as a machine-readable error code or a list of field errors, add fields here and update the helper in `GlobalExceptionHandler`. Keep the record immutable; create a new instance rather than mutating shared state.
