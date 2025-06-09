package model.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfigure {
    private static final String dbUrl = "jdbc:postgresql://34.50.101.249:5432/java";
    private static final String dbPassword = "matra1002";
    private static final String dbUsername = "postgres";
    public static Connection getDatabaseConnection(){
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        }catch (Exception exception){
            System.err.println("[!] ERROR during get connection with database: " + exception.getMessage());
        }
        return null;
    }
}
