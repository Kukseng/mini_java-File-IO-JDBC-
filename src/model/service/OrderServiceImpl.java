package model.service;

import model.entities.CartItem;
import model.repository.OrderRepository;

import java.util.List;

public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final CartService cartService;

    // Constructor-based injection
    public OrderService(OrderRepository orderRepository, CartServiceInterface cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    @Override
    public boolean placeOrder() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty!");
            return false;
        }

        Order order = orderRepository.createOrder(currentUser.getId(), cartItems);
        if (order != null) {
            cartService.clearCart();
            displayOrderConfirmation(order, cartItems);
            return true;
        } else {
            System.out.println("Failed to place order!");
            return false;
        }
    }

    private void displayOrderConfirmation(Order order, List<CartItem> cartItems) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ORDER CONFIRMATION");
        System.out.println("=".repeat(80));
        System.out.println("Order Code: " + order.getOrderCode());
        System.out.println("Order Date: " + LocalDateTime.now().toString().substring(0, 19));
        System.out.println("Customer: " + SessionManager.getCurrentUser().getUserName());
        System.out.println("-".repeat(80));

        int totalItems = 0;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            totalItems += item.getQuantity();
            System.out.printf("Product: %s | Quantity: %d | Price: $%.2f%n",
                    product.getName(), item.getQuantity(), product.getPrice());
        }

        System.out.println("-".repeat(80));
        System.out.println("Total Items: " + totalItems);
        System.out.printf("Total Price: $%.2f%n", order.getTotalPrice());
        System.out.println("=".repeat(80));
        System.out.println("Thank you for your order!");
    }
}
