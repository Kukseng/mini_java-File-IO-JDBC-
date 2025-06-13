package model.service;

import model.entities.Product;
import model.repository.ProductRepository;
import model.repository.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepositoryImpl();
    }

    public List<Product> getAllProducts(String category) {
        return productRepository.getAllProducts(category);
    }

    public List<Product> searchProductsByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts(null);
        }
        return productRepository.searchProductsByName(searchTerm.trim());
    }

    public List<Product> searchProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllProducts(null);
        }
        return productRepository.searchProductsByCategory(category.trim());
    }

    public Product getProductByUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return null;
        }
        return productRepository.getProductByUuid(uuid.trim());
    }

    public List<String> getAllCategories() {
        return productRepository.getAllCategories();
    }

    public void displayProductsByCategory() {
        List<String> categories = getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== AVAILABLE CATEGORIES ===");
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, categories.get(i));
        }
        System.out.println("0. View All Products");
        System.out.print("Select a category (0-" + categories.size() + "): ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Displaying all products.");
            choice = 0;
        }

        String selectedCategory = null;
        if (choice >= 1 && choice <= categories.size()) {
            selectedCategory = categories.get(choice - 1);
            System.out.println("\n--- " + selectedCategory.toUpperCase() + " ---");
        } else if (choice == 0) {
            System.out.println("\n--- ALL PRODUCTS ---");
        } else {
            System.out.println("Invalid choice. Displaying all products.");
        }

        List<Product> products = getAllProducts(selectedCategory);
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            displayProductList(products);
        }
    }

    public void displayProductList(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.printf("%-8s %-30s %-15s %-10s %-8s%n",
                "UUID", "Product Name", "Category", "Price", "Stock");
        System.out.println("─".repeat(80));

        for (Product product : products) {
            String shortUuid = product.getUuid().substring(0, 8);
            System.out.printf("%-8s %-30s %-15s $%-9.2f %-8d%n",
                    shortUuid,
                    truncateString(product.getName(), 30),
                    truncateString(product.getCategory(), 15),
                    product.getPrice(),
                    product.getQuantity());
        }
    }

    public List<Product> searchProducts(String searchTerm, String category) {
        List<Product> results = new ArrayList<>();

        if ((searchTerm == null || searchTerm.trim().isEmpty()) &&
                (category == null || category.trim().isEmpty())) {
            return getAllProducts(null);
        }

        if (category != null && !category.trim().isEmpty()) {
            results = searchProductsByCategory(category);

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                results = results.stream()
                        .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(java.util.stream.Collectors.toList());
            }
        } else {
            results = searchProductsByName(searchTerm);
        }

        return results;
    }

    public List<Product> getProductsWithPagination(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int offset = (page - 1) * pageSize;
        return ((ProductRepositoryImpl) productRepository).getProductsWithPagination(offset, pageSize);
    }

    public int getTotalProductCount() {
        return ((ProductRepositoryImpl) productRepository).getTotalProductCount();
    }

    public boolean addProduct(String name, String category, BigDecimal price, int quantity) {
        if (name == null || name.trim().isEmpty() ||
                category == null || category.trim().isEmpty() ||
                price == null || price.compareTo(BigDecimal.ZERO) <= 0 ||
                quantity < 0) {
            System.out.println("Invalid product data provided.");
            return false;
        }

        Product product = new Product(name.trim(), category.trim(), price, quantity);
        return ((ProductRepositoryImpl) productRepository).addProduct(product);
    }

    public boolean generateSampleProducts(int count) {
        System.out.println("Generating " + count + " sample products...");

        String[] categories = {"Electronics", "Clothing", "Books", "Home & Garden", "Sports",
                "Toys", "Beauty", "Automotive", "Food", "Health"};
        String[] adjectives = {"Premium", "Deluxe", "Standard", "Basic", "Professional",
                "Advanced", "Classic", "Modern", "Vintage", "Eco-friendly"};
        String[] nouns = {"Widget", "Gadget", "Tool", "Device", "Kit", "Set", "Pack",
                "Bundle", "Collection", "Series"};

        Random random = new Random();
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String category = categories[random.nextInt(categories.length)];
            String adjective = adjectives[random.nextInt(adjectives.length)];
            String noun = nouns[random.nextInt(nouns.length)];
            String name = adjective + " " + noun + " " + (i + 1);

            BigDecimal price = BigDecimal.valueOf(random.nextDouble() * 1000 + 1).setScale(2, BigDecimal.ROUND_HALF_UP);
            int quantity = random.nextInt(100) + 1;

            Product product = new Product(name, category, price, quantity);
            products.add(product);

            if (products.size() >= 1000) {
                if (!((ProductRepositoryImpl) productRepository).bulkInsertProducts(products)) {
                    System.err.println("Failed to insert batch at product " + (i + 1));
                    return false;
                }
                products.clear();

                if ((i + 1) % 10000 == 0) {
                    System.out.println("Inserted " + (i + 1) + " products...");
                }
            }
        }

        if (!products.isEmpty()) {
            if (!((ProductRepositoryImpl) productRepository).bulkInsertProducts(products)) {
                System.err.println("Failed to insert final batch");
                return false;
            }
        }

        System.out.println("Successfully generated " + count + " sample products!");
        return true;
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public boolean isProductAvailable(String uuid, int requestedQuantity) {
        Product product = getProductByUuid(uuid);
        return product != null && !product.getIsDeleted() &&
                product.getQuantity() >= requestedQuantity;
    }

    public boolean updateProductStock(String uuid, int quantityPurchased) {
        Product product = getProductByUuid(uuid);
        if (product == null || product.getQuantity() < quantityPurchased) {
            return false;
        }

        int newQuantity = product.getQuantity() - quantityPurchased;
        return ((ProductRepositoryImpl) productRepository).updateProductQuantity(uuid, newQuantity);
    }
}