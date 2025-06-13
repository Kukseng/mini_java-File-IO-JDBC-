package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private int id;
    private String p_name;
    private String category;
    private BigDecimal p_price;
    private int p_quantity;
    private String p_uuid;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
