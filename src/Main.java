import controller.MainController;
import model.service.AuthService;
import model.entities.User;

public class Main {
    public static void main(String[] args) {
        printBanner();

        // Attempt auto-login
        User loggedInUser = AuthService.getLoggedInUser();
        if (loggedInUser != null) {
            System.out.println("✅ Welcome back, " + loggedInUser.getUsername() + "!");
            System.out.println("🔄 Auto-login successful. Redirecting to main menu...");
        }

        // Start the main system
        MainController mainController = new MainController();
        mainController.start();

        // Exit message
        System.out.println("\n🛍️ Thank you for using E-Commerce System!");
        System.out.println("👋 Goodbye!");
    }

    private static void printBanner() {
        String line = "=".repeat(80);
        System.out.println(line);
        System.out.println("🛒 WELCOME TO E-COMMERCE SYSTEM 🛒");
        System.out.println(line);
        System.out.println("A comprehensive Java-based e-commerce application");
        System.out.println("Features: User Authentication, Product Management, Cart & Orders");
        System.out.println("Performance: Optimized for 10 million+ records");
        System.out.println(line);
    }
}
