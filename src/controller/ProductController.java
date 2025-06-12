package controller;

import model.entities.Product;
import model.service.ProductService;
import model.service.impl.ProductServiceImpl;

import java.util.List;

public class ProductController {
    private final ProductService service = new ProductServiceImpl();

    public void searchAndPrint(String keyword, String category) {
        List<Product> results = service.search(keyword, category);
        if (results.isEmpty()) {
            System.out.println("No products found.");
        } else {
            System.out.println("Search Results:");
            for (Product p : results) {
                System.out.printf("• %s (%s) - $%.2f\n", p.getP_name(), p.getCategory(), p.getPrice());
            }
        }
    }
}
