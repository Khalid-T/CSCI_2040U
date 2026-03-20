# Plant Catalogue System

Web app for browsing a 100-plant SQLite catalogue and letting admins maintain the data. Built with Javalin (Java 17), SQLite, and plain HTML/CSS.

## Team
Sara, Clinton, Fahad, Kevin, Khalid - CSCI 2040U (Software Design and Analysis)

## What's implemented right now
- User signup/login with session tracking (no password hashing yet).
- Admin-only add/remove of plants.
- Public search endpoint with filters by name or state.
- Static UI pages: catalogue (`index.html`), sign in/up, and admin dashboard.
- SQLite database bundled at `database/database.db` plus source CSV at `database/plants.csv`.
- JUnit 5 tests for core logic (`src/test/java/BackTest.java`).

Not implemented yet: password reset, wishlist persistence, recommendations, checkout/purchasing flow, or edit/update of existing plants/photos.

## Quick start
Prereqs: Java 17+, Maven 3.9+, SQLite JDBC downloads handled by Maven.

1) Install deps & build  
`mvn clean package`

2) Run the server from the repo root (serves static files and APIs)  
`mvn -Dexec.mainClass=back -Dexec.classpathScope=runtime exec:java`

3) Open the app  
`http://localhost:8080/signin.html` (login)  
`http://localhost:8080/index.html` (catalogue/search)  
`http://localhost:8080/admin.html` (admin tools)

Default admin user: `admin` / `admin` (stored in the SQLite DB). Create more users via sign-up.

## Project layout
- `src/main/java/back.java` - Javalin server and app logic.
- `src/main/resources/static/` - HTML/CSS/JS and images.
- `database/database.db` - live SQLite DB the app uses.
- `database/plants.csv` - source dataset (100 plants).
- `src/test/java/BackTest.java` - unit tests (in-memory SQLite).
- `pom.xml` - Maven config; dependencies: Javalin 6.1.3, sqlite-jdbc 3.45.1.0, slf4j-simple 2.0.9, JUnit 5.

## Running tests
`mvn test`

## API endpoints (server must be running)
- `POST /login-endpoint` - form login; redirects to catalogue or admin.
- `POST /signup-endpoint` - create standard user.
- `GET /logout` - clears session.
- `POST /add-plant` - admin only; adds a plant (symbol, scientific_name, common_name, state).
- `POST /remove-plant` - admin only; removes by common name.
- `GET /search-plants?q=...&type=name|state` - returns JSON array of plant rows.
- `GET /get-user` - returns current session username.

## Known limitations / next steps
- Passwords are stored in plaintext in SQLite; hashing and validation should be added.
- No CSRF protection; consider adding tokens for form posts.
- Admin check happens in Java but not in the HTML; hide admin UI when session is non-admin.
- Manual JSON building in `back.java` could be replaced with a JSON library (e.g., Jackson).
- Database file is mutable; add migrations/seed scripts if you need reproducible states.
