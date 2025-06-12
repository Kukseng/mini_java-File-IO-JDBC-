package model.service.impl;

import model.entities.Product;
import model.repository.ProductRepository;
import model.service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository = new ProductRepository();

    @Override
    public List<Product> search(String keyword, String category) {
        return productRepository.searchProducts(keyword, category);
    }

}
