Create a SQLite schema migration for this project.

## Context
This project uses SQLite via `sqlite-jdbc`. There is no migration framework — migrations are plain `.sql` files in `migrations/` following the convention `V<N>__<description>.sql`. Each file contains an `-- up` section and a `-- down` section.

## Steps

1. Ask the user (if not already specified in the prompt): what schema change is needed?
   Examples: "add column `phone` to users", "create table `sessions`", "rename column `age` to `birth_year`".

2. Check the current schema:
   - Read `migrations/` to find the highest existing version number (e.g. `V3__...sql` → next is `V4`).
   - If `migrations/` doesn't exist, this is the first migration: scaffold `migrations/` and start at `V1`.

3. Read the `User` model and `UserRepository` to understand current columns.

4. Generate the migration file `migrations/V<N>__<snake_case_description>.sql`:

```sql
-- up
ALTER TABLE users ADD COLUMN phone TEXT;

-- down
-- SQLite does not support DROP COLUMN before 3.35.0
-- Recreate table without the column if needed:
-- CREATE TABLE users_backup AS SELECT id, name, email, age FROM users;
-- DROP TABLE users;
-- ALTER TABLE users_backup RENAME TO users;
```

5. If the change affects the `User` model or `UserRepository`, show the Java diff needed (do not apply it automatically — ask first).

6. Remind the user to apply the migration manually:
   ```bash
   sqlite3 crud.sqlite < migrations/V<N>__<description>.sql
   ```
   and to run `mvn test` afterwards to verify nothing is broken.
