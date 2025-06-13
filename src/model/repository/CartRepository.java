package model.repository;


import model.entities.CartItem;

import java.util.List;

public interface CartRepository {
    boolean addToCart(int userId, String productUuid, int quantity);
    List<CartItem> getCartItems(int userId);
    boolean clearCart(int userId);
}

