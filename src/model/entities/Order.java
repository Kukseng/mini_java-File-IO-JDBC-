package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer id;
    private Integer userId;
    private String orderCode;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private List<OrderItem> orderItems;


    public Order(Integer userId, BigDecimal totalPrice) {
        this.userId = userId;
        this.orderCode = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.totalPrice = totalPrice;
        this.orderDate = LocalDateTime.now();
        this.orderItems = new ArrayList<>();
    }

    // Helper methods
    public void addOrderItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }

    public void calculateTotalPrice() {
        if (orderItems != null) {
            this.totalPrice = orderItems.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}

