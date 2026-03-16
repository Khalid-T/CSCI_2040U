# Plant Catalogue System

## Course

CSCI 2040U – Software Design and Analysis

## Team Members

* Sara 
* Clinton
* Fahad
* Kevin
* Khalid
---

## Project Overview

The Plant Catalogue System is a web application that lets administrators upload and manage plant information while users search and filter plants by characteristics (e.g., light, soil, region, price). The goal is to help people quickly locate plants suited to their needs without scrolling large catalogues.

## Dependencies

- slf4j-api-2.0.9.jar
- slf4j-simple-2.0.9.jar
- sqlite-jdbc-3.45.1.0.jar

---

## Core Features

### Admin Features
* **Inventory Management (Iteration 1):** Administrators can add new species to the database and remove plants that are no longer available.
* **Role-Based Access (Iteration 1):** Secure login/logout functionality that grants administrators access to the management dashboard.
* **Content Modification (Iteration 2):** Ability to update existing plant descriptions and replace/update plant photos for accuracy.
* **User Support (Iteration 2):** Access to basic user information to assist with account support and lockout issues.



### User Features
* **Regional Search (Iteration 1):** Users can search the 100-plant database by scientific name, common name, or specific US state.
* **Smart Filtering (Iteration 1):** Basic category filters to narrow down plant results based on attributes.
* **Account Security (Iteration 1/2):** Standard Login/Logout (Iter 1) and Password Reset functionality (Iter 2).
* **Personalization (Iteration 2/3):** Alphabetical sorting of results and a "Wishlist" system to save favorite plants.
* **Recommendation Engine (Iteration 3):** Automated plant suggestions based on user wishlist history and SQL-driven logic.

---

## Development Iterations

### Iteration 1: The "Demo Ready" Sprint (Labs 5–7)
* **Goal:** Establish a functional Minimum Viable Product (MVP).
* **Focus:** Core data handling and the admin-user loop.
* **Key Deliverables:** * Successful cleaning and integration of the 100-plant regional database.
    * Admin "Add/Remove" functionality.
    * User "Search/Filter" functionality.
    * Role-based Login/Logout system.



### Iteration 2: Quality & User Experience (Labs 8–10)
* **Goal:** Improve system flexibility, UI aesthetics, and account recovery.
* **Focus:** Refining the database schema to allow for "Updates" and polishing the frontend.
* **Key Deliverables:** * Password Reset workflow.
    * Update functionality for plant descriptions and photos.
    * Alphabetical sorting and UI Polish.
    * Unit testing on core classes and Statechart diagram design.

### Iteration 3: Final Features & Delivery (Labs 11–12)
* **Goal:** Implement advanced logic and final project hand-off.
* **Focus:** SQL-based recommendations and simulation of a purchasing flow.
* **Key Deliverables:** * Recommendation system based on user activity.
    * Purchasing/Checkout simulation.
    * Complete test suite utilizing Mock Objects for database isolation.
    * Final bug fixes and deployment preparation.

---

## Build & Run Instructions

You can start the Flora Catalogue server either directly through your IDE VS Code or by using the terminal with Maven.

### Option 1: IDE Method

1. **Open the Project:** Launch VS Code and ensure your project folder is open.
2. **Launch the Backend:** * Navigate to `src/main/java/back.java`.
   * Click the **Run** button located directly above the `public static void main` method (or press `F5`).
3. **Check the Terminal:** Look for the following confirmation message in the VS Code integrated terminal:
   ```text
   --- Flora Catalogue Server Running ---
   Go to: http://localhost:8080/signin.html

### Option 2: Maven / Terminal Method

1. **Prepare Environment:** Ensure JDK 17+ and Maven are installed.
2. **Clean Project:** Run `mvn clean` to remove old build artifacts.
3. **Install Dependencies:** Run `mvn install` to download required libraries (SQLite, SLF4J).
4. **Compile & Package:** Run `mvn package` to generate the executable.
5. **Launch:** Start the server using:
   ```bash
   mvn exec:java -Dexec.mainClass="back"

---

## Technologies (Planned)

* Frontend: HTML / CSS / JavaScript
* Backend:  Java 
* Version Control: Git + GitHub
* Database: SQL (e.g., MySQL / SQLite)

---


## Current Status

Login/search baseline works; add/remove and logout are underway; later iteration features (password reset, purchasing, recommendations) are still pending.
