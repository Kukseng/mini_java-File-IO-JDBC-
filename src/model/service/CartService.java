package model.service;

import model.entities.CartItem;
import model.entities.Product;
import model.entities.User;
import model.repository.CartRepositoryImpl;
import model.repository.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final CartRepositoryImpl cartRepository;
    private final ProductRepositoryImpl productRepository;

    public CartService() {
        this.cartRepository = new CartRepositoryImpl();
        this.productRepository = new ProductRepositoryImpl();
    }


    public boolean addToCart(String productUuid, int quantity) {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0!");
            return false;
        }

        Product product = productRepository.getProductByUuid(productUuid);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        if (product.getQuantity() < quantity) {
            System.out.println("Insufficient stock! Available: " + product.getQuantity());
            return false;
        }

        if (cartRepository.addToCart(currentUser.getId(), productUuid, quantity)) {
            System.out.println("Product '" + product.getName() + "' added to cart successfully!");
            return true;
        } else {
            System.out.println("Failed to add product to cart!");
            return false;
        }
    }


    public void displayCart() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<CartItem> cartItems = cartRepository.getCartItems(currentUser.getId());
        if (cartItems.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println("YOUR SHOPPING CART - " + currentUser.getUsername());
        System.out.println("=".repeat(90));
        System.out.printf("%-8s %-30s %-15s %-10s %-8s %-12s%n", 
                         "UUID", "Product Name", "Category", "Price", "Qty", "Subtotal");
        System.out.println("-".repeat(90));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            String shortUuid = product.getUuid().substring(0, 8);
            System.out.printf("%-8s %-30s %-15s $%-9.2f %-8d $%-11.2f%n",
                             shortUuid,
                             truncateString(product.getName(), 30),
                             truncateString(product.getCategory(), 15),
                             product.getPrice(),
                             item.getQuantity(),
                             itemTotal);
        }

        System.out.println("-".repeat(90));
        System.out.printf("TOTAL ITEMS: %d | TOTAL PRICE: $%.2f%n", cartItems.size(), totalPrice);
        System.out.println("=".repeat(90));
    }


    public List<CartItem> getCartItems() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return cartRepository.getCartItems(currentUser.getId());
    }


    public boolean clearCart() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        if (cartRepository.clearCart(currentUser.getId())) {
            System.out.println("Cart cleared successfully!");
            return true;
        } else {
            System.out.println("Failed to clear cart!");
            return false;
        }
    }

    /**
     * Remove specific item from cart
     */
    public boolean removeFromCart(String productUuid) {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        Product product = productRepository.getProductByUuid(productUuid);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        if (cartRepository.removeFromCart(currentUser.getId(), productUuid)) {
            System.out.println("Product '" + product.getName() + "' removed from cart!");
            return true;
        } else {
            System.out.println("Failed to remove product from cart!");
            return false;
        }
    }


    public boolean updateCartItemQuantity(String productUuid, int newQuantity) {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            System.out.println("Please login first!");
            return false;
        }

        if (newQuantity < 0) {
            System.out.println("Quantity cannot be negative!");
            return false;
        }

        if (newQuantity == 0) {
            return removeFromCart(productUuid);
        }

        Product product = productRepository.getProductByUuid(productUuid);
        if (product == null) {
            System.out.println("Product not found!");
            return false;
        }

        if (cartRepository.updateCartItemQuantity(currentUser.getId(), productUuid, newQuantity)) {
            System.out.println("Cart updated successfully!");
            return true;
        } else {
            System.out.println("Failed to update cart!");
            return false;
        }
    }

    public BigDecimal getCartTotal() {
        List<CartItem> cartItems = getCartItems();
        BigDecimal total = BigDecimal.ZERO;
        
        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        
        return total;
    }


    public int getCartItemCount() {
        User currentUser = AuthService.getLoggedInUser();
        if (currentUser == null) {
            return 0;
        }
        return cartRepository.getCartItemCount(currentUser.getId());
    }

    public boolean validateCart() {
        List<CartItem> cartItems = getCartItems();
        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty!");
            return false;
        }

        boolean isValid = true;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity()) {
                System.out.println("Insufficient stock for " + product.getName() + 
                                 ". Available: " + product.getQuantity() + 
                                 ", In cart: " + item.getQuantity());
                isValid = false;
            }
        }

        return isValid;
    }


    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}

