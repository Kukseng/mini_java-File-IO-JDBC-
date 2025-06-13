package model.repository;

import model.entities.CartItem;
import model.entities.Product;
import model.utils.DatabaseConfigure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepositoryImpl implements CartRepository {

    @Override
    public boolean addToCart(int userId, String productUuid, int quantity) {
        Product product = new ProductRepositoryImpl().getProductByUuid(productUuid);
        if (product == null) {
            System.out.println("Product not found with UUID: " + productUuid);
            return false;
        }

        if (product.getQuantity() < quantity) {
            System.out.println("Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + quantity);
            return false;
        }

        String checkSql = "SELECT id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            if (conn == null) {
                System.err.println("Database connection failed");
                return false;
            }

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, product.getId());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Item already exists in cart, update quantity
                    int currentQuantity = rs.getInt("quantity");
                    int totalQuantity = currentQuantity + quantity;

                    if (product.getQuantity() < totalQuantity) {
                        System.out.println("Insufficient stock for total quantity. Available: " + product.getQuantity() +
                                ", Current in cart: " + currentQuantity + ", Requested: " + quantity);
                        return false;
                    }

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, rs.getInt("id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // New item, insert into cart
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, product.getId());
                        insertStmt.setInt(3, quantity);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT ci.id as cart_id, ci.user_id, ci.product_id, ci.quantity as cart_quantity, ci.created_at as cart_created, " +
                "p.id as product_id, p.p_name, p.category, p.price, p.qty as product_quantity, p.p_uuid, p.is_deleted, p.created_at as product_created " +
                "FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.id " +
                "WHERE ci.user_id = ? AND p.is_deleted = FALSE " +
                "ORDER BY ci.created_at DESC";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getInt("cart_id"));
                cartItem.setUserId(rs.getInt("user_id"));
                cartItem.setProductId(rs.getInt("product_id"));
                cartItem.setQuantity(rs.getInt("cart_quantity"));

                Timestamp cartTimestamp = rs.getTimestamp("cart_created");
                if (cartTimestamp != null) {
                    cartItem.setCreatedAt(cartTimestamp.toLocalDateTime());
                }

                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("p_name"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setQuantity(rs.getInt("product_quantity"));
                product.setUuid(rs.getString("p_uuid"));
                product.setIsDeleted(rs.getBoolean("is_deleted"));

                Timestamp productTimestamp = rs.getTimestamp("product_created");
                if (productTimestamp != null) {
                    product.setCreatedAt(productTimestamp.toLocalDateTime());
                }

                cartItem.setProduct(product);
                cartItems.add(cartItem);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving cart items: " + e.getMessage());
        }
        return cartItems;
    }

    @Override
    public boolean clearCart(int userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int deletedRows = stmt.executeUpdate();
            System.out.println("Cleared " + deletedRows + " items from cart for user " + userId);
            return true;

        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }


    public boolean removeFromCart(int userId, String productUuid) {
        Product product = new ProductRepositoryImpl().getProductByUuid(productUuid);
        if (product == null) {
            return false;
        }

        String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, product.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }


    public boolean updateCartItemQuantity(int userId, String productUuid, int newQuantity) {
        if (newQuantity <= 0) {
            return removeFromCart(userId, productUuid);
        }

        Product product = new ProductRepositoryImpl().getProductByUuid(productUuid);
        if (product == null) {
            return false;
        }

        if (product.getQuantity() < newQuantity) {
            System.out.println("Insufficient stock. Available: " + product.getQuantity() + ", Requested: " + newQuantity);
            return false;
        }

        String sql = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, product.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
            return false;
        }
    }


    public int getCartItemCount(int userId) {
        String sql = "SELECT COUNT(*) FROM cart_items WHERE user_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting cart item count: " + e.getMessage());
        }
        return 0;
    }
}

