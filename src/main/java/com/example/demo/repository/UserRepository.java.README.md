# UserRepository.java

## Simple overview
This interface is the database access layer for users. Spring Data JPA writes the implementation automatically.

## Key behavior
- Provides standard CRUD operations through `JpaRepository<User, Long>`.
- `findByEmail` returns an `Optional<User>`.
- `existsByEmail` checks whether an email is already registered.
- Queries are derived from method names; there is no custom implementation.

## Related files
- `entity/User.java`

## Deep dive

### Inherited operations
`JpaRepository<User, Long>` provides standard persistence operations, including:

- `save` and `saveAll`
- `findById`, `findAll`, `findAllById`
- `existsById`
- `delete`, `deleteById`, `deleteAll`
- pagination and sorting helpers
- `flush` and `saveAndFlush`

The `Long` type parameter is the type of `User.id`.

### Derived queries
Spring Data generates query implementations from method names at startup:

- `findByEmail(String email)` becomes a query equivalent to `where u.email = ?1`. It returns `Optional.empty()` when no user matches.
- `existsByEmail(String email)` becomes an existence check, which avoids loading the full entity when only presence matters.

Because `email` is unique in the database, `findByEmail` should return at most one row. If the unique constraint is removed, this method would no longer be safe.

### Case sensitivity
The repository does not normalize or lowercase email addresses. Matching behavior follows the database collation. PostgreSQL is case-sensitive by default, so `User@example.com` and `user@example.com` can be treated as different values unless the schema or query applies a case-insensitive rule.

### Transactions
The repository inherits Spring Data transaction behavior. Simple CRUD methods are transactional individually. A business operation that reads and then writes should be placed in a service method with an appropriate transaction boundary, especially because `spring.jpa.open-in-view=false` closes the persistence context after the repository call returns.

### Current usage
No controller currently calls this repository. It is ready for future registration, login synchronization, or profile features. When adding such code, keep password handling and Auth0 subject mapping explicit rather than assuming the repository does it.
