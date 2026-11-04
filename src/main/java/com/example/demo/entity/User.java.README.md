# User.java

## Simple overview
This class represents a marketplace user in the database. It also stores which roles that user has.

## Key behavior
- Fields include names, unique email, password hash, optional date of birth, active flag, and creation time.
- `roles` is an eager `@ElementCollection` mapped to `user_roles`, with enum values stored as strings.
- `createdAt` defaults to the current instant; `active` defaults to `true`.
- `equals` and `hashCode` are based on the generated `id`.
- JPA uses `ddl-auto=validate`, so field mappings must match Flyway migrations.

## Related files
- `repository/UserRepository.java`
- `entity/Role.java`
- `src/main/resources/db/migration/V1__create_users_and_user_roles.sql`

## Deep dive

### Entity mapping
- `@Entity` and `@Table(name = "users")` map the class to the `users` table.
- `@Id` with `GenerationType.IDENTITY` matches the identity primary key in the Flyway migration. The database generates the ID on insert.
- Lombok generates getters, setters, and a no-argument constructor. JPA needs the no-argument constructor.

### Field-to-column mapping
Hibernate's naming strategy converts camel-case Java fields to snake-case database columns:

| Java field | Database column | Notes |
| --- | --- | --- |
| `id` | `id` | Identity primary key |
| `firstName` | `first_name` | Required, max length 50 |
| `lastName` | `last_name` | Required, max length 50 |
| `email` | `email` | Required, unique, max length 255 |
| `passwordHash` | `password_hash` | Required |
| `dateOfBirth` | `date_of_birth` | Nullable |
| `active` | `active` | Required, defaults to true in Java and SQL |
| `createdAt` | `created_at` | Required, not updatable after insert |

Because `ddl-auto=validate` is enabled, a mismatch between this table and the actual database columns prevents the application from starting. Any structural change needs a new Flyway migration.

### Roles collection
`roles` is not a normal entity relationship. It is an `@ElementCollection`, which means the role values live in a separate `user_roles` table owned by the user:

- `@CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))` defines the table and join column.
- `@Enumerated(EnumType.STRING)` stores values such as `BUYER` instead of ordinal numbers.
- The database primary key `(user_id, role)` prevents duplicate role rows.
- `FetchType.EAGER` means roles are loaded whenever a user is loaded. That keeps the object complete but can add extra queries for large user lists.

The `User` object does not expose a separate `UserRole` entity. The enum is the role model.

### `createdAt` behavior
`createdAt` is initialized in Java with `Instant.now()` and is marked `updatable = false`. The database column has no default value, so the application is responsible for supplying it on insert. After that, JPA will not include it in update statements.

### Equality behavior
`equals` and `hashCode` use the generated `id`:

- A saved user with an ID is equal to another user with the same ID.
- Two unsaved users with null IDs are not considered equal by this implementation, and both hash to 0.
- Avoid using unsaved `User` objects as keys in sets or maps, because their hash code changes once an ID is assigned.

### Current authentication context
The project currently authenticates with Auth0 and does not use `passwordHash` in the authentication flow. The column is still required by the schema, so code that creates users must provide a value. The `User` table is also not yet linked to the Auth0 subject in `AuthController`; that endpoint reads token claims only.
