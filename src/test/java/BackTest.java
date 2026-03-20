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
}
