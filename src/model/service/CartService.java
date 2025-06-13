package model.service;

import model.entities.CartItem;
import model.entities.Product;
import model.entities.User;
import model.repository.CartRepository;
import model.repository.CartRepositoryImpl;
import model.repository.ProductRepository;
import model.repository.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class CartService {
    private CartRepositoryImpl cartRepositoryImpl;
    private ProductRepository productRepositoryImpl;

    public CartService() {
        this.cartRepositoryImpl = new CartRepositoryImpl();
        this.productRepositoryImpl = new ProductRepositoryImpl();
    }

    public boolean addToCart(String productUuid, int quantity) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        Product product = productRepositoryImpl.getProductByUuid(productUuid);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        if (product.getQuantity() < quantity) {
            System.out.println("Insufficient stock! Available: " + product.getQuantity());
            return false;
        }

        if (cartRepositoryImpl.addToCart(currentUser.getId(), productUuid, quantity)) {
            System.out.println("Product added to cart successfully!");
            return true;
        } else {
            System.out.println("Failed to add product to cart!");
            return false;
        }
    }

    public void displayCart() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<CartItem> cartItems = cartRepositoryImpl.getCartItems(currentUser.getId());
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("YOUR SHOPPING CART");
        System.out.println("=".repeat(80));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            System.out.printf("Product: %s | Price: $%.2f | Quantity: %d | Subtotal: $%.2f%n",
                    product.getName(), product.getPrice(), item.getQuantity(), itemTotal);
        }

        System.out.println("-".repeat(80));
        System.out.printf("TOTAL: $%.2f%n", totalPrice);
        System.out.println("=".repeat(80));
    }

    public List<CartItem> getCartItems() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return cartRepositoryImpl.getCartItems(currentUser.getId());
    }

    public boolean clearCart() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return cartDAO.clearCart(currentUser.getId());
    }
}

