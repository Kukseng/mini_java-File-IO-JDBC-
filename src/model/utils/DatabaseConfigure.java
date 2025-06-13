package model.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfigure {
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String user = "postgres";
    private static String password = "supermatra";
    public static Connection getDatabaseConnection(){
        try {
            return DriverManager.getConnection(url, user, password);
        }catch (Exception exception){
            System.err.println("[!] ERROR during get connection with database: " + exception.getMessage());
        }
        return null;
    }
}