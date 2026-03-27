import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class back{
    private boolean is_admin =false;
    public Connection conn; // inialision conn to the database so i can acces it form anywhere
    public back() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:./database/database.db");
    }
    public void close() throws SQLException {
        conn.close();
    }
// ----------------------- reset password -------------------------------------------
  public String reset_password(String username, String password, String new_pass) throws SQLException {
      PreparedStatement check = conn.prepareStatement( "SELECT * FROM users WHERE username = ? AND password = ?");
      check.setString(1, username);
      check.setString(2, password);
      ResultSet rs = check.executeQuery();
      
     if (rs.next() == false) {
         System.out.println("[log] failed reset password for "+ username);
         return "Username or password is wrong";
     }
     PreparedStatement update = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
      update.setString(1, new_pass);
      update.setString(2, username);
      update.executeUpdate();

      System.out.println("[log] Password for "+ username + " has been updated");
      return "Password updated!";
  }
    
   //------------------------------------login --------------------------------------------
    public String sign_up(String username, String password, int  admin) throws SQLException {

        String insertSql = "INSERT INTO users (username, password, admin) VALUES (?, ?, ?)";
        
        PreparedStatement addusr = conn.prepareStatement(insertSql);

        addusr.setString(1, username);
        addusr.setString(2, password);
        addusr.setInt(3, admin);

        addusr.executeUpdate();

        if (admin == 0){
             System.out.println("[log] User " + username + " created (non-admin)");
             return "User " + username + " has been added and is not admin";

        }else{
             System.out.println("[log] User " + username + " created (non-admin)");
             return "User " + username + " has been added and is not admin";

        }
    } 
    public void logout(){
        System.out.println("[log] admin has logged off");
        is_admin = false; 
    }
    public boolean login(String username, String password) throws SQLException{

        PreparedStatement stmt = conn.prepareStatement( "SELECT * FROM users WHERE username = ? AND password = ?");
         stmt.setString(1,username);
         stmt.setString(2,password);

         boolean success = false;

         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
             success = true;
             is_admin = rs.getInt("admin") == 1;
         }

         rs.close();

         stmt.close();
         if (success) {
             System.out.println("[log] " + username + " has logged in");

             if (is_admin) {
                 System.out.println("[log] user is admin");
             } else {
                 System.out.println("[log] user is normal user");
             }

         } else {
             System.out.println("[log] " + username + " login didn't work");
         }

    return success;
    }
    //---------------------------------------------------------------


    // ------------------------- remove fuction-----------------------
    public String remove(String entry) throws SQLException{
        if (is_admin == false){
             System.out.println("[log] login first");
            return "login as an admin first";
        }
        PreparedStatement removed = conn.prepareStatement(
                    "DELETE FROM plants Where common_name = ?" );

        removed.setString(1,entry);

        int done = removed.executeUpdate();


        System.out.println("[log] removed " + done + " entr" + (done == 1 ? "y" : "ies"));

        removed.close();
        return "Removed " +entry+" form the database";
    }
    //-------------------------------------------------------------------------------------

    // ------------------------------------ add plants to database --------------------------
    public String  add(String Symbol,String SciName,String CommonName, String Region) throws SQLException{
        if (is_admin == false){
            System.out.println("[log] login first");
            return "login as admin before adding plants";
        }
        PreparedStatement added = conn.prepareStatement(
                                                        "INSERT INTO plants (symbol, scientific_name, common_name, state) VALUES (?, ?, ?, ?)" );

        added.setString(1, Symbol);
        added.setString(2, SciName);
        added.setString(3, CommonName);
        added.setString(4, Region);
        added.executeUpdate();

        System.out.println("[log] added "+ CommonName+ " to the database");

        added.close();
        return "\nadded "+ CommonName+ " to the list";

    }
    //-------------------------------------------------------------------------------



    //-------------------- SearchByName --------------------------------------------
    public List<String[]> searchByName(String name) throws SQLException {
        List<String[]> results = new ArrayList<>();
        String query = "SELECT symbol, scientific_name, common_name, state FROM plants WHERE LOWER(common_name) LIKE LOWER(?)";

        try (PreparedStatement searched = conn.prepareStatement(query)) {
            searched.setString(1, "%" + name + "%");  // adding % allows partial matches
            try (ResultSet rs = searched.executeQuery()) {
                while (rs.next()) {
                    results.add(new String[]{
                            rs.getString("symbol"), rs.getString("scientific_name"),
                            rs.getString("common_name"), rs.getString("state")
                        });
                }
            }
            return results;
        } catch (SQLException e) {
            System.out.println("Error searching by name: " + e.getMessage());
            return results;
        }
    }

    //-------------------- SearchByState --------------------------------------------
    public List<String[]> searchByState(String state) throws SQLException {
        List<String[]> results = new ArrayList<>();

        String query = "SELECT symbol, scientific_name, common_name, state FROM plants WHERE LOWER(state) LIKE LOWER(?)";

        try (PreparedStatement searched = conn.prepareStatement(query)) {
            searched.setString(1, "%" + state + "%"); // adding % allows partial matches
            try (ResultSet rs = searched.executeQuery()) {
                while (rs.next()) {
                    results.add(new String[]{
                            rs.getString("symbol"),
                            rs.getString("scientific_name"),
                            rs.getString("common_name"),
                            rs.getString("state")
                        });
                }
            }
        }
        return results;
    }
    public boolean isAdmin() {
        return is_admin;
    }
    public static void main(String[] args) throws Exception {
        //connection logic
        back appLogic = new back();

        //start the Javalin Server
        Javalin server = Javalin.create(config -> {
            // Tells Javalin to look in src/main/resources/static for HTML/CSS
            config.staticFiles.add("/static");
        }).start(8080);

        System.out.println("--- Flora Catalogue Server Running ---");
        System.out.println("Go to: http://localhost:8080/signin.html");

        //handle the Login Form Submission
        server.post("/login-endpoint", ctx -> {
            System.out.println(">>> LOGIN ATTEMPT RECEIVED <<<");

            String user = ctx.formParam("username");
            String pass = ctx.formParam("password");

            if (appLogic.login(user, pass)) {
                ctx.sessionAttribute("currentUser", user);

                if (appLogic.isAdmin()) {
                    ctx.redirect("/admin.html");
                } else {
                    ctx.redirect("/index.html");
                }
            } else {
                ctx.redirect("/signin.html?error=1");
            }
        });
        server.get("/logout", ctx ->{
            ctx.req().getSession().invalidate();
            appLogic.logout();
            ctx.redirect("/signin.html");
        });
        server.post("/add-plant", ctx -> {
            String symbol = ctx.formParam("symbol");
            String scientificName = ctx.formParam("scientific_name");
            String commonName = ctx.formParam("common_name");
            String state = ctx.formParam("state");

            String result = appLogic.add(symbol, scientificName, commonName, state);

            ctx.redirect("/admin.html?message=" + java.net.URLEncoder.encode(result, "UTF-8"));
        });

        server.post("/remove-plant", ctx -> {
            String commonName = ctx.formParam("common_name");

            String result = appLogic.remove(commonName);

            ctx.redirect("/admin.html?message=" + java.net.URLEncoder.encode(result, "UTF-8"));
        });
        server.get("/search-plants", ctx -> {
            String query = ctx.queryParam("q");
            String type = ctx.queryParam("type");

            System.out.println(">>> SEARCHING: " + query + " BY " + type);

            List<String[]> results;
            if ("state".equalsIgnoreCase(type)) {
                results = appLogic.searchByState(query);
            } else {
                results = appLogic.searchByName(query);
            }

            // --- ROBUST MANUAL JSON FIX ---
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < results.size(); i++) {
                String[] p = results.get(i);
                json.append("[");
                for (int j = 0; j < p.length; j++) {
                    // cleans the text so quotes and backslashes don't break the JSON
                    String cleaned = (p[j] == null) ? "" : p[j].replace("\\", "\\\\").replace("\"", "\\\"");
                    json.append("\"").append(cleaned).append("\"");
                    if (j < p.length - 1) json.append(",");
                }
                json.append("]");
                if (i < results.size() - 1) json.append(",");
            }
            json.append("]");

            ctx.contentType("application/json");
            ctx.result(json.toString());
        });

        //
        server.get("/get-user", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user != null) {
                ctx.result(user);
            } else {
                ctx.status(401);
            }
        });

        //redirect from sign up to sign in
        server.post("/signup-endpoint", ctx -> {
            String user = ctx.formParam("username");
            String pass = ctx.formParam("password");

            try {
                // Register the user as a normal user (admin = 0)
                appLogic.sign_up(user, pass, 0);


                ctx.redirect("/signin.html?registered=true");


                // ctx.sessionAttribute("currentUser", user);
                // ctx.redirect("/index.html");

            } catch (SQLException e) {
                // Redirect back to signup with an error if the username is taken
                ctx.redirect("/signup.html?error=exists");
            }
        });
    }


//    public  static void  main(String[] args) throws  Exception{
//
//        Class.forName("org.sqlite.JDBC");
//
//        back app = new back();
//
//        System.out.println("Connected to db \n\n");
//        //app.login("admin","admin");
//        app.add("test","test","test","test");
//        app.remove("test");
//
//        app.login("admin","admin");
//
//        app.add("test","test","test","test");
//
//        app.remove("test");
//        // Scanner input = new Scanner(System.in);
//
//
//        /*
//          while (true){
//          input.nextLine();
//          if (input.equals("quit")){
//          break;
//
//          }else if (input.equals("add")){
//
//
//          }
//
//
//          }
//          input.close();
//
//        */
//
//        app.close();
//    }
}
