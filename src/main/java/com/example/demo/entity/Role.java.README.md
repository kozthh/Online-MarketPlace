# Role.java

## Simple overview
This enum lists the possible roles a user can have. Right now there is only one role: buyer.

## Key behavior
- Currently contains only `BUYER`.
- Stored as a string in the `user_roles.role` column via `@Enumerated(EnumType.STRING)`.

## Related files
- `entity/User.java`
- `src/main/resources/db/migration/V1__create_users_and_user_roles.sql`

## Deep dive

### How the enum is stored
`User.roles` is an `@ElementCollection` of `Role` values. The `@Enumerated(EnumType.STRING)` annotation on that field means Hibernate stores the enum name, such as `BUYER`, rather than the enum's numeric ordinal.

This is important for database stability:

- Reordering enum constants does not change stored values.
- Renaming or removing an existing constant makes existing rows harder to read.
- Adding a new constant does not require a schema migration because the database column is `VARCHAR(255)`.

### Database representation
Each role assignment becomes one row in `user_roles`:

- `user_id` points to the user.
- `role` contains the enum name as text.
- The composite primary key `(user_id, role)` prevents the same role from being assigned twice to the same user.

There is no separate roles table. The allowed values exist only in this Java enum.

### Current use
The role is not used for authorization yet. `SecurityConfig` only checks whether a request is authenticated, not whether the user has `BUYER` or any other role. Adding role-based rules would require both new enum values and an authorization rule such as `hasRole(...)`.

### Adding roles safely
If you add a role:

1. Add the enum constant here.
2. Decide whether any authorization logic should use it.
3. Consider how existing users should obtain the new role.
4. Add tests for the new authorization behavior.

No Flyway migration is needed solely to widen the allowed values, because the database only stores text.
