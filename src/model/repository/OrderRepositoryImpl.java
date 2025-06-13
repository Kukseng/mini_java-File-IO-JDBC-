package model.repository;




import model.entities.CartItem;
import model.entities.Order;
import model.utils.DatabaseConfigure;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order createOrder(int userId, List<CartItem> cartItems) {
        String orderCode = "ORD-" + System.currentTimeMillis();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            totalPrice = totalPrice.add(item.getProduct().getP_price().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        String orderSql = "INSERT INTO orders (user_id, order_code, total_price) VALUES (?, ?, ?) RETURNING id";
        String orderItemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            conn.setAutoCommit(false);

            try {
                int orderId;
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

                try (PreparedStatement itemStmt = conn.prepareStatement(orderItemSql)) {
                    for (CartItem item : cartItems) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setBigDecimal(4, item.getProduct().getP_price());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                conn.commit();

                Order order = new Order();
                order.setId(orderId);
                order.setOrderDate(LocalDateTime.now());
                return order;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return null;
        }
    }
}
