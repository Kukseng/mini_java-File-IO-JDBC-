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

public interface OrderService {
    boolean placeOrder();
    void displayOrderHistory();
    Order getOrderByCode(String orderCode);
    void displayOrderDetails(String orderCode);
    List<Order> getUserOrders();
}

