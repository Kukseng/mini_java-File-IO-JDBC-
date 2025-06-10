package model.service;

import model.utils.DatabaseConfigure;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthService {
    private static final String SESSION_FILE = "/home/somatra/Documents/mini_java-File-IO-JDBC-/session.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static boolean login(String username, String password) {
        String sql = """
                SELECT u_uuid, password FROM users WHERE user_name = ?;
                """;
        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, username);
            ResultSet res = pre.executeQuery();
            if(res.next()){
                String uuid = res.getString("u_uuid");
                String hashedPassword = res.getString("password");

                if(PasswordUtil.checkPassword(password, hashedPassword)){
                    saveSession(uuid, hashedPassword);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[!] ERROR during login: " + e.getMessage());
        }
        return false;
    }

    public static void saveSession(String u_uuid, String hashedPassword) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        String content = u_uuid + "|" + hashedPassword + "|" + expiresAt.format(FORMATTER);
        File file = new File(SESSION_FILE);
        try {
            if (file.exists()) {
                file.setWritable(true);
            }
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content);
            }
            file.setReadOnly();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getLoggedInUser() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.split("\\|"); // split by pipe (escaped!)
                if (parts.length == 3) {
                    String uuid = parts[0];
                    String hashedPassword = parts[1]; // if needed
                    LocalDateTime expiresAt = LocalDateTime.parse(parts[2], FORMATTER);

                    if (LocalDateTime.now().isBefore(expiresAt)) {
                        return fetchUsernameByUuid(uuid);
                    } else {
                        logout();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void logout() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            if (file.setWritable(true) && file.delete()) {
                System.out.println("[+] Session cleared successfully");
            } else {
                System.out.println("[!] Failed to clear session: Permission denied or file in use");
            }
        } else {
            System.out.println("[!] No active session to clear");
        }
    }

    private static String fetchUsernameByUuid(String uuid) {
        String sql = "SELECT user_name FROM users WHERE u_uuid = ?";
        try (Connection con = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("user_name");
            }
        } catch (Exception e) {
            System.err.println("Error fetching username by UUID: " + e.getMessage());
        }
        return null;
    }

//        String user = AuthService.getLoggedInUser();
//        if (user != null) {
//            System.out.println("Auto-login as: " + user);
//            new HomeFrame(user);
//        } else {
//            new LoginFrame();
//        }


}