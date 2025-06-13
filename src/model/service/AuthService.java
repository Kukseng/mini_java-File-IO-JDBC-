package model.service;

import model.entities.User;
import model.repository.UserRepositoryImpl;
import model.utils.DatabaseConfigure;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuthService {
    private static final String SESSION_FILE = "/d/ecommerce-system-complete/ecommerce_session.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int SESSION_DURATION_MINUTES = 30;
    private static UserRepositoryImpl userRepository;

    static {
        userRepository = new UserRepositoryImpl();
    }

    public static boolean login(String username, String password) {
        username = username.trim().toLowerCase();
        System.out.println("[AuthService] Login attempt for username: '" + username + "'");
        System.out.println("[AuthService] DEBUG: Input password: '" + password + "'");

        User user = userRepository.findByUsername(username);

        if (user != null) {
            System.out.println("[AuthService] Found user with ID: " + user.getId() + ", Stored hash: " + user.getPassword());
            if (user.getPassword() == null) {
                System.out.println("[AuthService] [!] No password found for user: " + username);
                return false;
            }

            // DEBUG: Let's see what happens when we hash the input password
            String hashedInputPassword = PasswordUtil.hashPassword(password);
            System.out.println("[AuthService] DEBUG: Input password hashed: " + hashedInputPassword);

            // DEBUG: Let's also try checking the password
            boolean passwordMatches = PasswordUtil.checkPassword(password, user.getPassword());
            System.out.println("[AuthService] DEBUG: Password check result: " + passwordMatches);

            if (passwordMatches) {
                saveSession(user.getId(), user.getUuid(), user.getUsername(), user.getEmail());
                System.out.println("[AuthService] [+] Login successful for user: " + username);
                return true;
            } else {
                System.out.println("[AuthService] [!] Invalid password for user: " + username);

                // DEBUG: Let's try a direct comparison too
                System.out.println("[AuthService] DEBUG: Direct hash comparison: " + user.getPassword().equals(hashedInputPassword));
            }
        } else {
            System.out.println("[AuthService] [!] No user found: " + username);
        }
        return false;
    }

    public static boolean register(String username, String email, String password) {
        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();
        System.out.println("[AuthService] Register attempt for username: '" + username + "', email: '" + email + "'");
        System.out.println("[AuthService] DEBUG: Registration password: '" + password + "'");

        if (userRepository.findByUsername(username) != null) {
            System.out.println("[AuthService] [!] Username already exists: " + username);
            return false;
        }
        if (userRepository.findByEmail(email) != null) {
            System.out.println("[AuthService] [!] Email already exists: " + email);
            return false;
        }

        // DEBUG: Create user with plain password - let repository handle hashing
        User newUser = new User(username, email, password); // Plain password here
        System.out.println("[AuthService] DEBUG: User created with password: '" + newUser.getPassword() + "'");

        User savedUser = userRepository.save(newUser);
        if (savedUser != null) {
            System.out.println("[AuthService] [+] User registered successfully: " + username);
            System.out.println("[AuthService] DEBUG: Saved user password hash: " + savedUser.getPassword());
            return true;
        } else {
            System.out.println("[AuthService] [!] Failed to save user: " + username);
            return false;
        }
    }

    // ... rest of the methods remain the same ...

    private static void saveSession(int userId, String uuid, String username, String email) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES);
        String content = userId + "|" + uuid + "|" + username + "|" + email + "|" + expiresAt.format(FORMATTER);

        File file = new File(SESSION_FILE);
        try {
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content);
            }
            System.out.println("[AuthService] [+] Session saved successfully to: " + SESSION_FILE);
        } catch (IOException e) {
            System.err.println("[AuthService] [!] Error saving session to " + SESSION_FILE + ": " + e.getMessage());
        }
    }

    public static User getLoggedInUser() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) {
            System.out.println("[AuthService] No session file found at: " + SESSION_FILE);
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    int userId = Integer.parseInt(parts[0]);
                    String uuid = parts[1];
                    String username = parts[2];
                    String email = parts[3];
                    LocalDateTime expiresAt = LocalDateTime.parse(parts[4], FORMATTER);

                    if (LocalDateTime.now().isBefore(expiresAt)) {
                        User user = User.builder()
                                .id(userId)
                                .uuid(uuid)
                                .username(username)
                                .email(email)
                                .build();
                        System.out.println("[AuthService] Session valid for user: " + username);
                        return user;
                    } else {
                        logout();
                        System.out.println("[AuthService] [!] Session expired");
                    }
                } else {
                    System.out.println("[AuthService] Invalid session file format at: " + SESSION_FILE);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("[AuthService] [!] Error reading session from: " + SESSION_FILE + ": " + e.getMessage());
            logout();
        }
        return null;
    }

    public static boolean isLoggedIn() {
        return getLoggedInUser() != null;
    }

    public static void logout() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("[AuthService] [+] Logout successful - Session cleared from: " + SESSION_FILE);
            } else {
                System.out.println("[AuthService] [!] Failed to clear session file: " + SESSION_FILE);
            }
        }
    }

    public static boolean extendSession() {
        User user = getLoggedInUser();
        if (user != null) {
            saveSession(user.getId(), user.getUuid(), user.getUsername(), user.getEmail());
            return true;
        }
        return false;
    }
}