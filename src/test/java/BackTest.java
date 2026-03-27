import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class BackTest {

    private back app;

    @BeforeEach
    void setup() throws SQLException {
        // Use in-memory SQLite for isolated tests
        app = new back() {
            {
                // Override the connection to be in-memory
                conn = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:");
                java.sql.Statement stmt = conn.createStatement();
                // Create users table
                stmt.execute("""
                    CREATE TABLE users(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE,
                        password TEXT,
                        admin INTEGER DEFAULT 0
                    );
                """);
                // Create plants table
                stmt.execute("""
                    CREATE TABLE plants(
                        symbol TEXT,
                        scientific_name TEXT,
                        common_name TEXT,
                        state TEXT
                    );
                """);
                stmt.close();
            }
        };
    }

    @AfterEach
    void teardown() throws SQLException {
        app.close();
    }

    //--------------------- LOGIN TESTS -----------------------
    @Test
    void testLoginSuccess() throws SQLException {
        app.sign_up("user1", "pass", 0);
        assertTrue(app.login("user1", "pass"));
    }

    @Test
    void testLoginWrongPassword() throws SQLException {
        app.sign_up("user1", "pass", 0);
        assertFalse(app.login("user1", "wrong"));
    }

    @Test
    void testLoginUserNotFound() throws SQLException {
        assertFalse(app.login("ghost", "123"));
    }
    // ---------------------- password reset ---------------------
    @Test
    void testpasswordReset() throws SQLException {
        String result = app.reset_password("admin", "admin", "newpass");
        assertEquals("Password updated", result);
    }
    //--------------------- SIGN-UP TESTS ---------------------
    @Test
    void testSignupNormalUser() throws SQLException {
        String result = app.sign_up("John", "123", 0);
        assertTrue(result.contains("not admin"));
    }

    @Test
    void testSignupAdminUser() throws SQLException {
        String result = app.sign_up("admin2", "123", 1);
        assertTrue(result.contains("admin"));
    }

    @Test
    void testSignupDuplicateUser() throws SQLException {
        app.sign_up("Marco", "123", 0);
        assertThrows(SQLException.class, () -> {
            app.sign_up("Marco", "456", 0);
        });
    }

    //--------------------- LOGOUT TEST ----------------------
    @Test
    void testLogoutResetsAdmin() throws SQLException {
        app.sign_up("admin", "pass", 1);
        app.login("admin", "pass");
        app.logout();
        assertFalse(app.isAdmin());
    }

    //--------------------- ADD PLANT TESTS -------------------
    @Test
    void testAddWithoutAdmin() throws SQLException {
        String result = app.add("SYM", "Sci", "Rose", "CA");
        assertEquals("login as admin before adding plants", result);
    }

    @Test
    void testAddWithAdmin() throws SQLException {
        app.sign_up("admin", "pass", 1);
        app.login("admin", "pass");
        String result = app.add("SYM", "Sci", "Rose", "CA");
        assertTrue(result.contains("added Rose"));
    }

    //--------------------- REMOVE PLANT TESTS ----------------
    @Test
    void testRemoveWithoutAdmin() throws SQLException {
        String result = app.remove("Rose");
        assertEquals("login as an admin first", result);
    }
    @Test
    void testRemovePlantThatisNotThere() throws SQLException{
        app.sign_up("admin","pass",1);
        app.login("admin","pass");
        String res= app.remove("DragonPlant");
        assertTrue(res.contains("Removed"));
    }
    @Test
    void testRemoveWithAdmin() throws SQLException {
        app.sign_up("admin", "pass", 1);
        app.login("admin", "pass");
        app.add("SYM", "Sci", "Rose", "CA");
        String result = app.remove("Rose");
        assertTrue(result.contains("Removed"));
    }

    //--------------------- SEARCH TESTS ----------------------
    
    // helper method to seed the database
    private void seedSearchData() throws SQLException {
        String sql = "INSERT INTO plants (symbol, scientific_name, common_name, state) VALUES (?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = app.conn.prepareStatement(sql)) {
            
            // alabama samples
            stmt.setString(1, "ABTH");
            stmt.setString(2, "Abutilon theophrasti Medik.");
            stmt.setString(3, "VELVETLEAF");
            stmt.setString(4, "Alabama");
            stmt.addBatch();

            stmt.setString(1, "ACRU");
            stmt.setString(2, "Acer rubrum L.");
            stmt.setString(3, "RED MAPLE");
            stmt.setString(4, "Alabama");
            stmt.addBatch();

            // arizona samples
            stmt.setString(1, "ABAN");
            stmt.setString(2, "Abronia angustifolia Greene");
            stmt.setString(3, "purple sand-verbena");
            stmt.setString(4, "Arizona");
            stmt.addBatch();

            // nevada samples
            stmt.setString(1, "ABBA");
            stmt.setString(2, "Abies balsamea (L.) Mill.");
            stmt.setString(3, "balsam fir");
            stmt.setString(4, "Nevada");
            stmt.addBatch();

            stmt.executeBatch();
        }
    }

    @Test
    void testSearchByNameFound() throws SQLException {
        seedSearchData();
        // verifies the search can find a specific common name
        java.util.List<String[]> results = app.searchByName("RED MAPLE");
        assertFalse(results.isEmpty());
        assertEquals("ACRU", results.get(0)[0]); 
    }

    @Test
    void testSearchByStateAlabama() throws SQLException {
        seedSearchData();
        // since we seeded 1 Alabama plants, we expect exactly 2 back
        java.util.List<String[]> results = app.searchByState("Alabama");
        assertEquals(2, results.size(), "Alabama should return 2 seeded results");
    }

    @Test
    void testSearchByStateNevada() throws SQLException {
        seedSearchData();
        // verifies the state filter works for different regions
        java.util.List<String[]> results = app.searchByState("Nevada");
        assertEquals(1, results.size());
        assertEquals("balsam fir", results.get(0)[2]);
    }

    @Test
    void testSearchByStateNoResults() throws SQLException {
        seedSearchData();
        // verifies that a state not in the "10 states" list returns an empty list
        java.util.List<String[]> results = app.searchByState("California");
        assertTrue(results.isEmpty(), "Should be empty for states not in our 10-state list");
    }

    @Test
    void testSearchByStateCaseSensitivity() throws SQLException {
        seedSearchData();
        // testing if your SQL query is case-sensitive for state names
        java.util.List<String[]> results = app.searchByState("arizona");
        
        assertFalse(results.isEmpty(), "Search should ideally be case-insensitive for user ease");
    }
}
