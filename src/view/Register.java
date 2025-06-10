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

        String hashedPassword = PasswordUtil.hashPassword(password);

        CreateUserDto dto = CreateUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .build();

        UserRespondDto response = controller.createNewUser(dto);

        if (response != null && response.uuid() != null) {
            AuthService.saveSession(response.uuid(), hashedPassword);
            System.out.println("Registration successful!");
            // Assuming Home class exists for console-based navigation
            new Home_Testing_Auth(response.username());
        } else {
            System.out.println("Registration failed! Email or username might already exist.");
        }
    }
}