package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private int qty;
    private BigDecimal price;
    private Product product;
}
