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

public interface OrderRepository {
    Order createOrder(int userId, List<CartItem> cartItems);
    List<Order> getOrdersByUserId(int userId);
    Order getOrderById(int orderId);
    Order getOrderByCode(String orderCode);
    List<OrderItem> getOrderItems(int orderId);
    boolean updateOrderStatus(int orderId, String status);
}

