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
            return false;
        }

        String checkSql = "SELECT id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, product.getId());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, rs.getInt("id"));
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
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
        String sql = "SELECT ci.*, p.* FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.id " +
                "WHERE ci.user_id = ? AND p.is_deleted = FALSE";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getInt("ci.id"));
                cartItem.setUserId(rs.getInt("ci.user_id"));
                cartItem.setProductId(rs.getInt("ci.product_id"));
                cartItem.setQuantity(rs.getInt("ci.quantity"));

                Product product = new Product();
                product.setId(rs.getInt("p.id"));
                product.setP_name(rs.getString("p.p_name"));
                product.setCategory(rs.getString("p.category"));
                product.setP_price(rs.getBigDecimal("p.price"));
                product.setP_quantity(rs.getInt("p.qty"));
                product.setP_uuid(rs.getString("p.p_uuid"));

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
            return stmt.executeUpdate() >= 0;

        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }
}

