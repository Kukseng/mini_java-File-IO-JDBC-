package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private Product product;
    private LocalDateTime createdAt;


    public CartItem(Integer userId, Integer productId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }
}

