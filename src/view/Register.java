package view;

import controller.UserController;
import model.dto.CreateUserDto;
import model.dto.UserRespondDto;
import model.service.AuthService;
import model.service.PasswordUtil;

import java.util.Scanner;

public class Register {
    private final UserController controller = new UserController();
    private final Scanner scanner = new Scanner(System.in);

    public void startRegistration() {
        System.out.println("=== User Registration ===");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        // Use AuthService.register directly
        if (AuthService.register(username, email, password)) {
            System.out.println("Registration successful! You can now login.");
            // No need to call saveSession here, it's handled by AuthService.register
            // Assuming Home_Testing_Auth is for testing purposes and not part of main flow
            // new Home_Testing_Auth(username); // This line might need to be removed or adjusted based on actual UI flow
        } else {
            System.out.println("Registration failed! Email or username might already exist.");
        }
    }
}

