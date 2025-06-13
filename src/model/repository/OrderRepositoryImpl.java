package model.repository;

import model.entities.CartItem;
import model.entities.Order;
import model.entities.OrderItem;
import model.entities.Product;
import model.utils.DatabaseConfigure;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order createOrder(int userId, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            System.err.println("Cannot create order with empty cart");
            return null;
        }

        String orderCode = "ORD-" + System.currentTimeMillis();
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Calculate total price
        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        String orderSql = "INSERT INTO orders (user_id, order_code, total_price) VALUES (?, ?, ?) RETURNING id";
        String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE products SET qty = qty - ? WHERE id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            if (conn == null) {
                System.err.println("Database connection failed");
                return null;
            }

            conn.setAutoCommit(false);

            try {
                int orderId;

                // Create order
                try (PreparedStatement orderStmt = conn.prepareStatement(orderSql)) {
                    orderStmt.setInt(1, userId);
                    orderStmt.setString(2, orderCode);
                    orderStmt.setBigDecimal(3, totalPrice);

                    ResultSet rs = orderStmt.executeQuery();
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to create order");
                    }
                }

                // Create order items and update stock
                try (PreparedStatement itemStmt = conn.prepareStatement(orderItemSql);
                     PreparedStatement stockStmt = conn.prepareStatement(updateStockSql)) {

                    for (CartItem item : cartItems) {
                        // Insert order item
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setBigDecimal(4, item.getProduct().getPrice());
                        itemStmt.addBatch();

                        // Update product stock
                        stockStmt.setInt(1, item.getQuantity());
                        stockStmt.setInt(2, item.getProductId());
                        stockStmt.addBatch();
                    }

                    itemStmt.executeBatch();
                    stockStmt.executeBatch();
                }

                conn.commit();

                // Create and return order object
                Order order = new Order();
                order.setId(orderId);
                order.setUserId(userId);
                order.setOrderCode(orderCode);
                order.setTotalPrice(totalPrice);
                order.setOrderDate(LocalDateTime.now());

                System.out.println("Order created successfully with ID: " + orderId + ", Code: " + orderCode);
                return order;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error creating order, transaction rolled back: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving orders for user: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving order by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Order getOrderByCode(String orderCode) {
        String sql = "SELECT * FROM orders WHERE order_code = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving order by code: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT oi.*, p.p_name, p.category, p.p_uuid " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setId(rs.getInt("id"));
                orderItem.setOrderId(rs.getInt("order_id"));
                orderItem.setProductId(rs.getInt("product_id"));
                orderItem.setQuantity(rs.getInt("quantity"));
                orderItem.setPrice(rs.getBigDecimal("price"));

                // Create product object with basic info
                Product product = new Product();
                product.setId(rs.getInt("product_id"));
                product.setName(rs.getString("p_name"));
                product.setCategory(rs.getString("category"));
                product.setUuid(rs.getString("p_uuid"));
                product.setPrice(rs.getBigDecimal("price"));

                orderItem.setProduct(product);
                orderItems.add(orderItem);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving order items: " + e.getMessage());
        }
        return orderItems;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String status) {

        System.out.println("Order status update not implemented (requires schema modification)");
        return true;
    }


    public int getOrderCountByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting order count: " + e.getMessage());
        }
        return 0;
    }

    public BigDecimal getTotalSpentByUserId(int userId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM orders WHERE user_id = ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total spent: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderCode(rs.getString("order_code"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));

        Timestamp timestamp = rs.getTimestamp("order_date");
        if (timestamp != null) {
            order.setOrderDate(timestamp.toLocalDateTime());
        }

        return order;
    }
}

