package model.repository;


import model.entities.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> getAllProducts();
    List<Product> searchProductsByName(String searchTerm);
    List<Product> searchProductsByCategory(String category);
    Product getProductByUuid(String uuid);
    List<String> getAllCategories();
}
