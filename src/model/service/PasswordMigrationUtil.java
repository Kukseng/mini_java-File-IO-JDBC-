package model.service;

import model.entities.User;
import model.repository.UserRepositoryImpl;
import java.sql.*;
import model.utils.DatabaseConfigure;

public class PasswordMigrationUtil {

    public static void main(String[] args) {
        migrateUserPassword("a", "a"); // username, plain password
    }

    public static void migrateUserPassword(String username, String plainPassword) {
        System.out.println("=== PASSWORD MIGRATION UTILITY ===");
        System.out.println("Migrating password for user: " + username);

        // Hash the password with current algorithm
        String newHash = PasswordUtil.hashPassword(plainPassword);
        System.out.println("New hash: " + newHash);

        // Update in database
        String sql = "UPDATE users SET password = ? WHERE user_name = ?";

        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("Database connection failed");
                return;
            }

            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, newHash);
            pre.setString(2, username);

            int rowsAffected = pre.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Password updated successfully for user: " + username);

                // Verify the update worked
                testLogin(username, plainPassword);
            } else {
                System.out.println("❌ No user found with username: " + username);
            }

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }

    private static void testLogin(String username, String password) {
        System.out.println("\n--- TESTING LOGIN AFTER MIGRATION ---");
        UserRepositoryImpl userRepo = new UserRepositoryImpl();
        User user = userRepo.findByUsername(username);

        if (user != null) {
            System.out.println("User found: " + user.getUsername());
            System.out.println("Stored hash: " + user.getPassword());

            boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPassword());
            System.out.println("Password check result: " + passwordMatches);

            if (passwordMatches) {
                System.out.println("✅ Migration successful! Login should now work.");
            } else {
                System.out.println("❌ Migration failed. Password still doesn't match.");
            }
        }
    }
}