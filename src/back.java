import java.sql.*;
import java.util.Scanner;

public class back{
    private Connection conn;

    public back() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:database/plants.db");
    }
    public void close() throws SQLException {
        conn.close();
    }

    private void add(String Name,String Family,String Genus, String plantOrder, String Kingdom, String pH) throws SQLException{

        PreparedStatement added = conn.prepareStatement(
                                                        "INSERT INTO plants (Name, Family, Genus, PlantOrder, Kingdom, pH) VALUES (?, ?, ?, ?, ?, ?)"
                                                        );
        added.setString(1, Name);
        added.setString(2, Family);
        added.setString(3, Genus);
        added.setString(4, plantOrder);
        added.setString(5, Kingdom);
        added.setString(6, pH);
        added.executeUpdate();

        System.out.println("added "+Name +" to the list");

        added.close();
    }


    public  static void  main(String[] args) throws  Exception{

        Class.forName("org.sqlite.JDBC");

        back app = new back();

        System.out.println("Connected to db \n\n tpye quit to stop");

        Scanner input = new Scanner(System.in);
        String command;
        while (true){
            command = input.nextLine();
            if (command.equals("quit")){
                break;

            }else if (command.equals("add")){


            }
            

        }
        input.close();
        app.close();
    }
}
