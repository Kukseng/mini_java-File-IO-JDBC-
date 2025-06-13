package view;

import model.service.AuthService;

import java.util.Scanner;

public class Login {
    private final Scanner scanner = new Scanner(System.in);

    public void start_login() {
        System.out.println("=== dLogin ===");

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (AuthService.login(username, password)) {
            System.out.println("Login successful");
            new Home_Testing_Auth(username);
        } else {
            System.out.println("Login failed");
        }

    }
}