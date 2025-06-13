package model.repository;


import lombok.Data;
import model.entities.CartItem;
import model.entities.Order;

import java.util.List;

public interface OrderRepository {
    Order createOrder(int userId, List<CartItem> cartItems);
}











