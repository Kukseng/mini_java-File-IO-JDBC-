package view;

import model.service.AuthService;

import java.util.Scanner;

public class Home_Testing_Auth {
    private final Scanner scanner = new Scanner(System.in);

    public Home_Testing_Auth(String username) {
        System.out.println("=== Welcome ===");
        System.out.println("Welcome, " + username + "!");
        System.out.println("1. Logout");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {
            AuthService.logout();
            System.out.println("Logged out successfully.");
            new Login(); // Assuming Login class exists for console-based navigation
        }
    }
}