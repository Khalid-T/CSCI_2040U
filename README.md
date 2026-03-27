# Plant Catalogue System

## Course

CSCI 2040U – Software Design and Analysis

## Team Members

- Sara
- Clinton
- Fahad
- Kevin
- Khalid

---

## Project Overview

The Plant Catalogue System is a web application that lets administrators upload and manage plant information while users search and filter plants by characteristics (e.g., light, soil, region, price). The goal is to help people quickly locate plants suited to their needs without scrolling large catalogues.

## Prerequisites

- JDK 17+
- Maven 3.9+
- Project dependencies are managed via `pom.xml`.

---

## Core Features

### Admin Features

- **Inventory Management (Iteration 1):** Administrators can add new species to the database and remove plants that are no longer available.
- **Role-Based Access (Iteration 1):** Secure login/logout functionality that grants administrators access to the management dashboard.
- **Content Modification (Iteration 2):** Ability to update existing plant descriptions and replace/update plant photos for accuracy.
- **User Support (Iteration 2):** Access to basic user information to assist with account support and lockout issues.

### User Features

- **Regional Search (Iteration 1):** Users can search the 100-plant database by scientific name, common name, or specific US state.
- **Smart Filtering (Iteration 1):** Basic category filters to narrow down plant results based on attributes.
- **Account Security (Iteration 1/2):** Standard Login/Logout (Iter 1) and Password Reset functionality (Iter 2).
- **Personalization (Iteration 2/3):** Alphabetical sorting of results and a "Wishlist" system to save favorite plants.
- **Recommendation Engine (Iteration 3):** Automated plant suggestions based on user wishlist history and SQL-driven logic.

---

## Development Iterations

### Iteration 1: The "Demo Ready" Sprint (Labs 5–7)

- **Goal:** Establish a functional Minimum Viable Product (MVP).
- **Focus:** Core data handling and the admin-user loop.
- **Key Deliverables:**
  - Successful cleaning and integration of the 100-plant regional database.
  - Admin "Add/Remove" functionality.
  - User "Search/Filter" functionality.
  - Role-based Login/Logout system.

### Iteration 2: Quality & User Experience (Labs 8–10)

- **Goal:** Improve system flexibility, UI aesthetics, and account recovery.
- **Focus:** Refining the database schema to allow for "Updates" and polishing the frontend.
- **Key Deliverables:**
  - Password Reset workflow.
  - Update functionality for plant descriptions and photos.
  - Alphabetical sorting and UI Polish.
  - Unit testing on core classes and Statechart diagram design.

### Iteration 3: Final Features & Delivery (Labs 11–12)

- **Goal:** Implement advanced logic and final project hand-off.
- **Focus:** SQL-based recommendations and simulation of a purchasing flow.
- **Key Deliverables:**
  - Recommendation system based on user activity.
  - Purchasing/Checkout simulation.
  - Complete test suite utilizing Mock Objects for database isolation.
  - Final bug fixes and deployment preparation.

---

## Build & Run Instructions

You can start the Plant Catalogue server either directly through your IDE VS Code, by using the terminal with Maven or running `build_project.bat`.

### Option 1: IDE Method

1. **Open the Project:** Launch VS Code and ensure your project folder is open.
2. **Launch the Backend:**
   - Navigate to `src/main/java/back.java`.
   - Click the **Run** button located directly above the `public static void main` method (or press `F5`).
3. **Check the Terminal:** Look for the following confirmation message in the VS Code integrated terminal:
   ```text
   --- Flora Catalogue Server Running ---
   Go to: http://localhost:8080/signin.html
   ```

### Option 2: Maven / Terminal Method

1. **Prepare Environment:** Ensure JDK 17+ and Maven 3.9+ are installed and on your PATH.

2. **Clone the Repository:**

```bash
   git clone https://github.com/Khalid-T/FloraFinder
   cd FloraFinder
```

3. **Compile and Run** using one of the following:

   **Via the build script:**
   Navigate to `build_project.bat` in the project root and double-click it.
   This will compile the project, copy dependencies into `lib/` on first run, and host the site on port 8080.

   **Via Maven directly:**

```bash
   mvn clean compile exec:java
```

This clears old build artifacts, recompiles `back.java`, and hosts the site on port 8080.
Maven resolves dependencies automatically from its local cache — the `lib/` folder is not used.

4. **Check the Terminal:** Look for the following confirmation message:

```text
   --- Flora Catalogue Server Running ---
   Go to: http://localhost:8080/signin.html
```

5. **Stop the Server:** Press `Ctrl+C` in the terminal.

> **Note:** The `build_project.bat` script copies dependencies into `lib/` on first run only — subsequent runs will skip this step automatically.

---

## Technologies (Planned)

- Frontend: HTML / CSS / JavaScript
- Backend: Java
- Version Control: Git + GitHub
- Database: SQL (e.g., MySQL / SQLite)

---

## Current Status

Login, search, add, remove, logout, login all work; sign up is almost finished; Reset password being worked on; later iteration features are still being designed .
