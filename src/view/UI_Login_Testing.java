package view;

import model.service.AuthService;
import model.entities.User;

import java.util.Scanner;

public class UI_Login_Testing {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        home();
    }

    public static void home() {
        while (true) {
            User loggedInUser = AuthService.getLoggedInUser();
            if (loggedInUser != null) {
                System.out.println("[+] Session found for user: " + loggedInUser.getUsername());
                new Home_Testing_Auth(loggedInUser.getUsername());
            }
            Header.header();
            System.out.println("=== Main Menu ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("[+] Select option: ");

            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[!] Invalid option :(");
                waitForEnter();
                continue;
            }

            switch (choice) {
                case 1 -> new Login().start_login();
                case 2 -> new Register().startRegistration();
                case 3 -> {
                    System.out.println("Exiting program...");
                    System.exit(0);
                }
                default -> System.out.println("[!] Invalid option :(");
            }
            waitForEnter();
        }
    }

    private static void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

