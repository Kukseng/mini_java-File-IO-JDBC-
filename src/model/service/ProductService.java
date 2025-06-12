
package model.service;

import model.entities.Product;
import java.util.List;

public interface ProductService {
    List<Product> search(String keyword, String category);
}
