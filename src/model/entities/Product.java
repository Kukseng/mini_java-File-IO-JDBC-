package model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String p_uuid;
    private String p_name;
    private String category;
    private double price;
    private int qty;
    private boolean is_deleted;
}
