package controller;

import model.service.*;
import model.entities.User;
import java.util.Scanner;

public class MainController {
    private final Scanner scanner;
    private final AuthService authService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderServiceImpl orderService;
    private final PerformanceService performanceService;
    private boolean running;

    public MainController() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService();
        this.productService = new ProductService();
        this.cartService = new CartService();
        this.orderService = new OrderServiceImpl();
        this.performanceService = new PerformanceService();
        this.running = true;
    }

    public void start() {
        while (running) {
            User currentUser = AuthService.getLoggedInUser();

            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu(currentUser);
            }
        }
        scanner.close();
    }

    private void showGuestMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GUEST MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. View All Products");
        System.out.println("4. Search Products");
        System.out.println("5. Performance Tests (Admin)");
        System.out.println("0. Exit");
        System.out.println("=".repeat(50));
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 3:
                    productService.displayProductsByCategory();
                    break;
                case 4:
                    handleProductSearch();
                    break;
                case 5:
                    handlePerformanceTests();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void showUserMenu(User user) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("MAIN MENU - Welcome, " + user.getUsername() + "!");
        System.out.println("=".repeat(60));
        System.out.println("1. View By Category");
        System.out.println("2. Search Products");
        System.out.println("3. View Shopping Cart");
        System.out.println("4. Add Product to Cart");
        System.out.println("5. Place Order");
        System.out.println("6. View Order History");
        System.out.println("7. View Order Details");
        System.out.println("8. Performance Tests ");
        System.out.println("9. Account Settings");
        System.out.println("0. Logout");
        System.out.println("=".repeat(60));
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    productService.displayProductsByCategory();
                    break;
                case 2:
                    handleProductSearch();
                    break;
                case 3:
                    cartService.displayCart();
                    showCartMenu();
                    break;
                case 4:
                    handleAddToCart();
                    break;
                case 5:
                    handlePlaceOrder();
                    break;
                case 6:
                    orderService.displayOrderHistory();
                    break;
                case 7:
                    handleViewOrderDetails();
                    break;

                case 8:
                    handlePerformanceTests();
                    break;
                case 9:
                    showAccountSettings(user);
                    break;
                case 0:
                    handleLogout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void showCartMenu() {
        if (!AuthService.isLoggedIn()) return;

        System.out.println("\nCart Actions:");
        System.out.println("1. Update item quantity");
        System.out.println("2. Remove item from cart");
        System.out.println("3. Clear cart");
        System.out.println("4. Proceed to checkout");
        System.out.println("0. Back to main menu");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleUpdateCartQuantity();
                    break;
                case 2:
                    handleRemoveFromCart();
                    break;
                case 3:
                    cartService.clearCart();
                    break;
                case 4:
                    handlePlaceOrder();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (AuthService.login(username, password)) {
            System.out.println("Login successful! Welcome, " + username + "!");
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    private void handleRegister() {
        System.out.println("\n--- REGISTER ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (AuthService.register(username, email, password)) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private void handleLogout() {
        AuthService.logout();
        System.out.println("Logged out successfully!");
    }

    private void handleProductSearch() {
        System.out.println("\n--- PRODUCT SEARCH ---");
        System.out.println("1. Search by name");
        System.out.println("2. Search by category");
        System.out.print("Choose search type: ");

        try {
            int searchType = Integer.parseInt(scanner.nextLine().trim());

            switch (searchType) {
                case 1:
                    System.out.print("Enter product name (or part of it): ");
                    String name = scanner.nextLine().trim();
                    productService.displayProductList(productService.searchProductsByName(name));
                    break;
                case 2:
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine().trim();
                    productService.displayProductList(productService.searchProductsByCategory(category));
                    break;
                default:
                    System.out.println("Invalid search type.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private void handleAddToCart() {
        System.out.println("\n--- ADD TO CART ---");
        System.out.print("Enter product UUID (first 8 characters): ");
        String shortUuid = scanner.nextLine().trim();

        String fullUuid = findProductUuidByShortUuid(shortUuid);
        if (fullUuid == null) {
            System.out.println("Product not found with UUID: " + shortUuid);
            return;
        }

        System.out.print("Enter quantity: ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            cartService.addToCart(fullUuid, quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a number.");
        }
    }

    private void handleUpdateCartQuantity() {
        System.out.print("Enter product UUID (first 8 characters): ");
        String shortUuid = scanner.nextLine().trim();

        String fullUuid = findProductUuidByShortUuid(shortUuid);
        if (fullUuid == null) {
            System.out.println("Product not found with UUID: " + shortUuid);
            return;
        }

        System.out.print("Enter new quantity (0 to remove): ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            cartService.updateCartItemQuantity(fullUuid, quantity);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a number.");
        }
    }

    private void handleRemoveFromCart() {
        System.out.print("Enter product UUID (first 8 characters): ");
        String shortUuid = scanner.nextLine().trim();

        String fullUuid = findProductUuidByShortUuid(shortUuid);
        if (fullUuid == null) {
            System.out.println("Product not found with UUID: " + shortUuid);
            return;
        }

        cartService.removeFromCart(fullUuid);
    }

    private void handlePlaceOrder() {
        System.out.println("\n--- PLACE ORDER ---");

        if (cartService.getCartItemCount() == 0) {
            System.out.println("Your cart is empty. Add some products first!");
            return;
        }

        cartService.displayCart();
        System.out.print("Confirm order? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("y") || confirm.equals("yes")) {
            orderService.placeOrder();
        } else {
            System.out.println("Order cancelled.");
        }
    }

    private void handleViewOrderDetails() {
        System.out.print("Enter order code: ");
        String orderCode = scanner.nextLine().trim();
        orderService.displayOrderDetails(orderCode);
    }

    private void showAccountSettings(User user) {
        System.out.println("\n--- ACCOUNT SETTINGS ---");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("User ID: " + user.getId());
        System.out.println("UUID: " + user.getUuid());

        System.out.println("\nAccount Actions:");
        System.out.println("1. Extend session");
        System.out.println("0. Back to main menu");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 1) {
                if (AuthService.extendSession()) {
                    System.out.println("Session extended successfully!");
                } else {
                    System.out.println("Failed to extend session.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void handlePerformanceTests() {
        System.out.println("\n--- PERFORMANCE TESTS ---");
        System.out.println("1. Insert 10 Million Products");
        System.out.println("2. Read 10 Million Products");
        System.out.println("3. Search Performance Test");
        System.out.println("0. Back to main menu");
        System.out.print("Choose an option: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    System.out.print("Are you sure? This will insert 10 million products (y/n): ");
                    if (scanner.nextLine().trim().toLowerCase().startsWith("y")) {
                        performanceService.insert10MillionProducts();
                    }
                    break;
                case 2:
                    performanceService.read10MillionProducts();
                    break;
                case 3:
                    performanceService.performanceTestSearch();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private String findProductUuidByShortUuid(String shortUuid) {
        var allProducts = productService.getAllProducts(null); // Fixed: Pass null for all products
        for (var product : allProducts) {
            if (product.getUuid().startsWith(shortUuid)) {
                return product.getUuid();
            }
        }
        return null;
    }
}