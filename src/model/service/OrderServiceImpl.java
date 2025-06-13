package model.service;

import model.entities.CartItem;
import model.entities.Order;
import model.entities.OrderItem;
import model.entities.Product;
import model.entities.User;
import model.repository.OrderRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderRepositoryImpl orderRepository;
    private final CartService cartService;
    private final ProductService productService;

    public OrderServiceImpl() {
        this.orderRepository = new OrderRepositoryImpl();
        this.cartService = new CartService();
        this.productService = new ProductService();
    }

    @Override
    public boolean placeOrder() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty!");
            return false;
        }

        // Validate cart before placing order
        if (!cartService.validateCart()) {
            System.out.println("Cart validation failed. Please check product availability.");
            return false;
        }

        // Create order
        Order order = orderRepository.createOrder(currentUser.getId(), cartItems);
        if (order != null) {
            // Clear cart after successful order
            cartService.clearCart();
            
            // Display order confirmation
            displayOrderConfirmation(order, cartItems, currentUser);
            return true;
        } else {
            System.out.println("Failed to place order! Please try again.");
            return false;
        }
    }

    @Override
    public void displayOrderHistory() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<Order> orders = orderRepository.getOrdersByUserId(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("You have no order history.");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("ORDER HISTORY - " + currentUser.getUsername());
        System.out.println("=".repeat(100));
        System.out.printf("%-15s %-20s %-12s %-15s%n", 
                         "Order Code", "Order Date", "Total Price", "Status");
        System.out.println("-".repeat(100));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Order order : orders) {
            totalSpent = totalSpent.add(order.getTotalPrice());
            System.out.printf("%-15s %-20s $%-11.2f %-15s%n",
                             order.getOrderCode(),
                             order.getOrderDate().format(formatter),
                             order.getTotalPrice(),
                             "Completed");
        }

        System.out.println("-".repeat(100));
        System.out.printf("Total Orders: %d | Total Spent: $%.2f%n", orders.size(), totalSpent);
        System.out.println("=".repeat(100));
    }

    @Override
    public Order getOrderByCode(String orderCode) {
        if (orderCode == null || orderCode.trim().isEmpty()) {
            return null;
        }
        return orderRepository.getOrderByCode(orderCode.trim());
    }

    @Override
    public void displayOrderDetails(String orderCode) {
        Order order = getOrderByCode(orderCode);
        if (order == null) {
            System.out.println("Order not found with code: " + orderCode);
            return;
        }

        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null || !order.getUserId().equals(currentUser.getId())) {
            System.out.println("Access denied. You can only view your own orders.");
            return;
        }

        List<OrderItem> orderItems = orderRepository.getOrderItems(order.getId());
        
        System.out.println("\n" + "=".repeat(90));
        System.out.println("ORDER DETAILS");
        System.out.println("=".repeat(90));
        System.out.println("Order Code: " + order.getOrderCode());
        System.out.println("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Customer: " + currentUser.getUsername());
        System.out.println("-".repeat(90));
        System.out.printf("%-8s %-30s %-15s %-10s %-8s %-12s%n", 
                         "UUID", "Product Name", "Category", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(90));

        int totalItems = 0;
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalItems += item.getQuantity();

            String shortUuid = product.getUuid().substring(0, 8);
            System.out.printf("%-8s %-30s %-15s $%-9.2f %-8d $%-11.2f%n",
                             shortUuid,
                             truncateString(product.getName(), 30),
                             truncateString(product.getCategory(), 15),
                             item.getPrice(),
                             item.getQuantity(),
                             subtotal);
        }

        System.out.println("-".repeat(90));
        System.out.printf("Total Items: %d | Total Price: $%.2f%n", totalItems, order.getTotalPrice());
        System.out.println("=".repeat(90));
    }

    @Override
    public List<Order> getUserOrders() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            return List.of();
        }
        return orderRepository.getOrdersByUserId(currentUser.getId());
    }

    private void displayOrderConfirmation(Order order, List<CartItem> cartItems, User user) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println("🎉 ORDER CONFIRMATION 🎉");
        System.out.println("=".repeat(90));
        System.out.println("Order Code: " + order.getOrderCode());
        System.out.println("Order Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("Customer: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("-".repeat(90));
        System.out.printf("%-30s %-15s %-10s %-8s %-12s%n", 
                         "Product Name", "Category", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(90));

        int totalItems = 0;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalItems += item.getQuantity();

            System.out.printf("%-30s %-15s $%-9.2f %-8d $%-11.2f%n",
                             truncateString(product.getName(), 30),
                             truncateString(product.getCategory(), 15),
                             product.getPrice(),
                             item.getQuantity(),
                             subtotal);
        }

        System.out.println("-".repeat(90));
        System.out.printf("Number of Products: %d%n", cartItems.size());
        System.out.printf("Total Items: %d%n", totalItems);
        System.out.printf("Total Price: $%.2f%n", order.getTotalPrice());
        System.out.println("=".repeat(90));
        System.out.println("Thank you for your order! Your items will be processed shortly.");
        System.out.println("Order Code: " + order.getOrderCode() + " (Save this for your records)");
        System.out.println("=".repeat(90));
    }


    public void displayOrderStatistics() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        int orderCount = orderRepository.getOrderCountByUserId(currentUser.getId());
        BigDecimal totalSpent = orderRepository.getTotalSpentByUserId(currentUser.getId());

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ORDER STATISTICS - " + currentUser.getUsername());
        System.out.println("=".repeat(60));
        System.out.println("Total Orders: " + orderCount);
        System.out.printf("Total Spent: $%.2f%n", totalSpent);
        
        if (orderCount > 0) {
            BigDecimal averageOrder = totalSpent.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP);
            System.out.printf("Average Order Value: $%.2f%n", averageOrder);
        }
        
        System.out.println("=".repeat(60));
    }


    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}

