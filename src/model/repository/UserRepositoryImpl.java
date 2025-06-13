package model.repository;

import model.entities.User;
import model.service.PasswordUtil;
import model.utils.DatabaseConfigure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository<User, Integer> {

    @Override
    public User save(User user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
        System.out.println("[UserRepositoryImpl] Saving user: " + user.getUsername() + ", Hashed password: " + hashedPassword);
        String sql = "INSERT INTO users (u_uuid, user_name, email, password, is_deleted, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("[UserRepositoryImpl] Database connection failed");
                return null;
            }
            PreparedStatement pre = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pre.setString(1, user.getUuid());
            pre.setString(2, user.getUsername());
            pre.setString(3, user.getEmail());
            pre.setString(4, hashedPassword);
            pre.setBoolean(5, user.getIsDeleted());
            pre.setTimestamp(6, Timestamp.valueOf(user.getCreatedAt()));

            int rowAffected = pre.executeUpdate();
            if (rowAffected > 0) {
                try (ResultSet generatedKeys = pre.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("[UserRepositoryImpl] User has been inserted successfully: " + user.getUsername());
                return user;
            }
        } catch (SQLException exception) {
            System.err.println("[UserRepositoryImpl] Error during insert data to table user: " + exception.getMessage());
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, u_uuid, user_name, email, is_deleted, created_at FROM users WHERE is_deleted = FALSE";

        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("[UserRepositoryImpl] Database connection failed");
                return users;
            }
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUuid(rs.getString("u_uuid"));
                user.setUsername(rs.getString("user_name"));
                user.setEmail(rs.getString("email"));
                user.setIsDeleted(rs.getBoolean("is_deleted"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                users.add(user);
            }
        } catch (SQLException exception) {
            System.err.println("[UserRepositoryImpl] Error during fetching all users: " + exception.getMessage());
        }
        return users;
    }

    @Override
    public Integer delete(Integer id) {
        String sql = "UPDATE users SET is_deleted = TRUE WHERE id = ?";
        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("[UserRepositoryImpl] Database connection failed");
                return 0;
            }
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setInt(1, id);
            int rowAffected = pre.executeUpdate();
            if (rowAffected > 0) {
                System.out.println("[UserRepositoryImpl] User with ID " + id + " has been marked as deleted.");
                return id;
            }
        } catch (SQLException exception) {
            System.err.println("[UserRepositoryImpl] Error during deleting user: " + exception.getMessage());
        }
        return 0;
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, u_uuid, user_name, email, password, is_deleted, created_at FROM users WHERE user_name = ? AND is_deleted = FALSE";
        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("[UserRepositoryImpl] Database connection failed");
                return null;
            }
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, username);
            System.out.println("[UserRepositoryImpl] Executing query for username: " + username);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUuid(rs.getString("u_uuid"));
                user.setUsername(rs.getString("user_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setIsDeleted(rs.getBoolean("is_deleted"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                System.out.println("[UserRepositoryImpl] Found user: " + user.getUsername());
                return user;
            } else {
                System.out.println("[UserRepositoryImpl] No user found for username: " + username);
            }
        } catch (SQLException exception) {
            System.err.println("[UserRepositoryImpl] Error during finding user by username: " + exception.getMessage());
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, u_uuid, user_name, email, password, is_deleted, created_at FROM users WHERE email = ? AND is_deleted = FALSE";
        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            if (con == null) {
                System.err.println("[UserRepositoryImpl] Database connection failed");
                return null;
            }
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, email);
            System.out.println("[UserRepositoryImpl] Executing query for email: " + email);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUuid(rs.getString("u_uuid"));
                user.setUsername(rs.getString("user_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setIsDeleted(rs.getBoolean("is_deleted"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                System.out.println("[UserRepositoryImpl] Found user: " + user.getUsername());
                return user;
            } else {
                System.out.println("[UserRepositoryImpl] No user found for email: " + email);
            }
        } catch (SQLException exception) {
            System.err.println("[UserRepositoryImpl] Error during finding user by email: " + exception.getMessage());
        }
        return null;
    }
}