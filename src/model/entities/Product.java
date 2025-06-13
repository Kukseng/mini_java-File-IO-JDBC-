package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String uuid;
    private Boolean isDeleted;
    private LocalDateTime createdAt;


    public Product(String name, String category, BigDecimal price, Integer quantity) {
        this.uuid = UUID.randomUUID().toString();
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }
}

