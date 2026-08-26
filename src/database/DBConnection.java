package database;

import java.sql.*;

public class DBConnection {

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/studentdb",
                    "root",
                    "Nithish@0517");

            System.out.println("Database Connected Successfully!");

        } catch (Exception e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }

        return con;
    }
}