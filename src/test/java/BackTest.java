import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
 
import java.sql.SQLException;
import java.util.List;
 
public class BackTest {
 
    private back app;
 
    @BeforeEach
    void setup() throws SQLException {
        app = new back() {
            {
                conn = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:");
                java.sql.Statement stmt = conn.createStatement();
                stmt.execute("""
                    CREATE TABLE users(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE,
                        password TEXT,
                        admin INTEGER DEFAULT 0
                    );
                """);
                // Updated schema with new filter columns
                stmt.execute("""
                    CREATE TABLE plants(
                        symbol TEXT,
                        scientific_name TEXT,
                        common_name TEXT,
                        state TEXT,
                        light_requirement TEXT,
                        water_requirement TEXT,
                        plant_type TEXT
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
    void testRemovePlantThatisNotThere() throws SQLException {
        app.sign_up("admin", "pass", 1);
        app.login("admin", "pass");
        String res = app.remove("DragonPlant");
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
 
    // Seed helper — now includes light, water, and plant_type
    private void seedSearchData() throws SQLException {
        String sql = "INSERT INTO plants (symbol, scientific_name, common_name, state, light_requirement, water_requirement, plant_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = app.conn.prepareStatement(sql)) {
 
            // Alabama — Full Sun, Low, Herb
            stmt.setString(1, "ABTH");
            stmt.setString(2, "Abutilon theophrasti Medik.");
            stmt.setString(3, "VELVETLEAF");
            stmt.setString(4, "Alabama");
            stmt.setString(5, "Full Sun");
            stmt.setString(6, "Low");
            stmt.setString(7, "Herb");
            stmt.addBatch();
 
            // Alabama — Partial Shade, Moderate, Tree
            stmt.setString(1, "ACRU");
            stmt.setString(2, "Acer rubrum L.");
            stmt.setString(3, "RED MAPLE");
            stmt.setString(4, "Alabama");
            stmt.setString(5, "Partial Shade");
            stmt.setString(6, "Moderate");
            stmt.setString(7, "Tree");
            stmt.addBatch();
 
            // Arizona — Full Sun, Moderate, Grass
            stmt.setString(1, "ABAN");
            stmt.setString(2, "Abronia angustifolia Greene");
            stmt.setString(3, "purple sand-verbena");
            stmt.setString(4, "Arizona");
            stmt.setString(5, "Full Sun");
            stmt.setString(6, "Moderate");
            stmt.setString(7, "Grass");
            stmt.addBatch();
 
            // Nevada — Full Shade, High, Tree
            stmt.setString(1, "ABBA");
            stmt.setString(2, "Abies balsamea (L.) Mill.");
            stmt.setString(3, "balsam fir");
            stmt.setString(4, "Nevada");
            stmt.setString(5, "Full Shade");
            stmt.setString(6, "High");
            stmt.setString(7, "Tree");
            stmt.addBatch();
 
            // Nevada — Full Sun, Low, Herb  (second nevada entry to test multi-result filters)
            stmt.setString(1, "ARDI");
            stmt.setString(2, "Aristida divaricata Humb.");
            stmt.setString(3, "poverty threeawn");
            stmt.setString(4, "Nevada");
            stmt.setString(5, "Full Sun");
            stmt.setString(6, "Low");
            stmt.setString(7, "Herb");
            stmt.addBatch();
 
            stmt.executeBatch();
        }
    }
 
    // --- Name search ---
 
    @Test
    void testSearchByNameFound() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("RED MAPLE", null, null, null, null);
        assertFalse(results.isEmpty());
        assertEquals("ACRU", results.get(0)[0]);
    }
 
    @Test
    void testSearchByNamePartialMatch() throws SQLException {
        seedSearchData();
        // "maple" should match "RED MAPLE"
        List<String[]> results = app.searchWithFilters("maple", null, null, null, null);
        assertFalse(results.isEmpty());
        assertEquals("RED MAPLE", results.get(0)[2]);
    }
 
    @Test
    void testSearchByNameNoMatch() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("dragon plant", null, null, null, null);
        assertTrue(results.isEmpty());
    }
 
    @Test
    void testSearchByNameCaseInsensitive() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("velvetleaf", null, null, null, null);
        assertFalse(results.isEmpty(), "Name search should be case-insensitive");
    }
 
    @Test
    void testSearchEmptyNameReturnsAll() throws SQLException {
        seedSearchData();
        // Empty name with no filters should return everything
        List<String[]> results = app.searchWithFilters("", null, null, null, null);
        assertEquals(5, results.size());
    }
 
    // --- State filter ---
 
    @Test
    void testFilterByStateAlabama() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", "Alabama", null, null, null);
        assertEquals(2, results.size(), "Alabama should return 2 seeded results");
    }
 
    @Test
    void testFilterByStateNevada() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", "Nevada", null, null, null);
        assertEquals(2, results.size());
    }
 
    @Test
    void testFilterByStateNoResults() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", "California", null, null, null);
        assertTrue(results.isEmpty(), "Should be empty for a state not in the seeded data");
    }
 
    @Test
    void testFilterByStateCaseInsensitive() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", "arizona", null, null, null);
        assertFalse(results.isEmpty(), "State filter should be case-insensitive");
    }
 
    // --- Light filter ---
 
    @Test
    void testFilterByLightFullSun() throws SQLException {
        seedSearchData();
        // VELVETLEAF (Alabama), purple sand-verbena (Arizona), poverty threeawn (Nevada)
        List<String[]> results = app.searchWithFilters("", null, "Full Sun", null, null);
        assertEquals(3, results.size());
    }
 
    @Test
    void testFilterByLightFullShade() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", null, "Full Shade", null, null);
        assertEquals(1, results.size());
        assertEquals("balsam fir", results.get(0)[2]);
    }
 
    @Test
    void testFilterByLightNoResults() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", null, "Underwater", null, null);
        assertTrue(results.isEmpty());
    }
 
    // --- Water filter ---
 
    @Test
    void testFilterByWaterLow() throws SQLException {
        seedSearchData();
        // VELVETLEAF (Alabama) and poverty threeawn (Nevada) are both Low
        List<String[]> results = app.searchWithFilters("", null, null, "Low", null);
        assertEquals(2, results.size());
    }
 
    @Test
    void testFilterByWaterHigh() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", null, null, "High", null);
        assertEquals(1, results.size());
        assertEquals("balsam fir", results.get(0)[2]);
    }
 
    // --- Plant type filter ---
 
    @Test
    void testFilterByPlantTypeTree() throws SQLException {
        seedSearchData();
        // RED MAPLE (Alabama) and balsam fir (Nevada)
        List<String[]> results = app.searchWithFilters("", null, null, null, "Tree");
        assertEquals(2, results.size());
    }
 
    @Test
    void testFilterByPlantTypeHerb() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", null, null, null, "Herb");
        assertEquals(2, results.size());
    }
 
    @Test
    void testFilterByPlantTypeNoResults() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("", null, null, null, "Cactus");
        assertTrue(results.isEmpty());
    }
 
    // --- Combined name + filter ---
 
    @Test
    void testNameAndStateFilter() throws SQLException {
        seedSearchData();
        // "fir" in Nevada should return only balsam fir
        List<String[]> results = app.searchWithFilters("fir", "Nevada", null, null, null);
        assertEquals(1, results.size());
        assertEquals("balsam fir", results.get(0)[2]);
    }
 
    @Test
    void testNameAndStateMismatch() throws SQLException {
        seedSearchData();
        // "fir" exists but not in Alabama — should return nothing
        List<String[]> results = app.searchWithFilters("fir", "Alabama", null, null, null);
        assertTrue(results.isEmpty());
    }
 
    @Test
    void testNameAndLightFilter() throws SQLException {
        seedSearchData();
        // "maple" exists but is Partial Shade — searching Full Sun should miss it
        List<String[]> results = app.searchWithFilters("maple", null, "Full Sun", null, null);
        assertTrue(results.isEmpty());
    }
 
    @Test
    void testNameAndWaterFilter() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("VELVETLEAF", null, null, "Low", null);
        assertEquals(1, results.size());
        assertEquals("ABTH", results.get(0)[0]);
    }
 
    @Test
    void testNameAndPlantTypeFilter() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("maple", null, null, null, "Tree");
        assertEquals(1, results.size());
        assertEquals("RED MAPLE", results.get(0)[2]);
    }
 
    // --- All filters combined ---
 
    @Test
    void testAllFiltersMatch() throws SQLException {
        seedSearchData();
        // poverty threeawn: Nevada, Full Sun, Low, Herb
        List<String[]> results = app.searchWithFilters("threeawn", "Nevada", "Full Sun", "Low", "Herb");
        assertEquals(1, results.size());
        assertEquals("ARDI", results.get(0)[0]);
    }
 
    @Test
    void testAllFiltersNoMatch() throws SQLException {
        seedSearchData();
        // Correct name but wrong combination of filters
        List<String[]> results = app.searchWithFilters("balsam fir", "Nevada", "Full Sun", "Low", "Herb");
        assertTrue(results.isEmpty(), "balsam fir is Full Shade/High/Tree, not Full Sun/Low/Herb");
    }
 
    // --- Return format ---
 
    @Test
    void testResultArrayHasSevenFields() throws SQLException {
        seedSearchData();
        List<String[]> results = app.searchWithFilters("RED MAPLE", null, null, null, null);
        assertFalse(results.isEmpty());
        assertEquals(7, results.get(0).length,
            "Each result should have 7 fields: symbol, scientific_name, common_name, state, light, water, plant_type");
    }
}