# Online-MarketPlace

Spring Boot marketplace backend with a static Auth0 login page.

## Requirements

- Java 21
- No Node.js or frontend build step is required
- Windows: use `mvnw.cmd`
- macOS/Linux: use `./mvnw`

## Run locally (recommended: dev profile)

The `dev` profile uses an in-memory H2 database, so PostgreSQL is not required.

### Windows Command Prompt (`cmd.exe`)

```cmd
cd /d "D:\Repositories\Marketplace\Online-MarketPlace"
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### macOS / Linux / WSL with JDK 21 installed

```bash
./mvnw --% spring-boot:run -Dspring-boot.run.profiles=dev
```

Wait until you see:

```text
Tomcat started on port 8080 (http)
Started McplaceApplication in ... seconds
```

Then open:

```text
http://localhost:8080
```

Click **Log in** to reach the Auth0 login page.

## Stop the application

### If it is running in the same terminal

1. Press `Ctrl+C`.
2. If prompted to terminate the batch job, press `Y`.

### If it was started in the background or the terminal was closed

Find the process using port `8080` and stop it.

Windows `cmd.exe`:

```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Replace `<PID>` with the process ID from the last column of the `netstat` output.

As a last resort for local development, stop all Java processes:

### Ctrl + C

```cmd
taskkill /IM java.exe /F
```

macOS / Linux:

```bash
lsof -i :8080
kill <PID>
```

## Default profile (PostgreSQL)

Without the `dev` profile, the app expects PostgreSQL at `localhost:5432/mcplace` and credentials in `DB_USERNAME` / `DB_PASSWORD`:

```cmd
mvnw.cmd spring-boot:run
```

## Auth0 configuration

The login flow uses the committed development Auth0 tenant. The following URLs must be registered in the Auth0 application:

- Allowed Callback URL: `http://localhost:8080`
- Allowed Logout URL: `http://localhost:8080`

If login fails with a callback or redirect error, add these URLs in the Auth0 dashboard.
