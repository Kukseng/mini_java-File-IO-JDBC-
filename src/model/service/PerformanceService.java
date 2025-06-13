package model.service;

import model.entities.Product;
import model.repository.ProductRepositoryImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PerformanceService {
    private final ProductRepositoryImpl productRepository;
    private final ProductService productService;

    public PerformanceService() {
        this.productRepository = new ProductRepositoryImpl();
        this.productService = new ProductService();
    }


    public boolean insert10MillionProducts() {
        System.out.println("Starting insertion of 10 million products...");
        System.out.println("This operation may take several minutes depending on your system.");
        
        long startTime = System.currentTimeMillis();
        
        // Use multi-threading for better performance
        int threadCount = Runtime.getRuntime().availableProcessors();
        int productsPerThread = 10_000_000 / threadCount;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Boolean>> futures = new ArrayList<>();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            final int startIndex = i * productsPerThread;
            final int endIndex = (i == threadCount - 1) ? 10_000_000 : (i + 1) * productsPerThread;
            
            Future<Boolean> future = executor.submit(() -> {
                return insertProductsBatch(startIndex, endIndex, threadIndex);
            });
            futures.add(future);
        }
        
        // Wait for all threads to complete
        boolean allSuccess = true;
        for (Future<Boolean> future : futures) {
            try {
                if (!future.get()) {
                    allSuccess = false;
                }
            } catch (Exception e) {
                System.err.println("Thread execution failed: " + e.getMessage());
                allSuccess = false;
            }
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Executor shutdown interrupted: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        if (allSuccess) {
            System.out.println("Successfully inserted 10 million products!");
            System.out.printf("Total time: %.2f seconds%n", duration / 1000.0);
            System.out.printf("Average rate: %.0f products/second%n", 10_000_000.0 / (duration / 1000.0));
        } else {
            System.err.println("Failed to insert all products. Check logs for details.");
        }
        
        return allSuccess;
    }

    /**
     * Insert a batch of products for a specific thread
     */
    private boolean insertProductsBatch(int startIndex, int endIndex, int threadIndex) {
        String[] categories = {"Electronics", "Clothing", "Books", "Home & Garden", "Sports", 
                              "Toys", "Beauty", "Automotive", "Food", "Health", "Music", 
                              "Movies", "Games", "Office", "Pet Supplies"};
        String[] adjectives = {"Premium", "Deluxe", "Standard", "Basic", "Professional", 
                              "Advanced", "Classic", "Modern", "Vintage", "Eco-friendly",
                              "Luxury", "Budget", "Smart", "Wireless", "Portable"};
        String[] nouns = {"Widget", "Gadget", "Tool", "Device", "Kit", "Set", "Pack", 
                         "Bundle", "Collection", "Series", "System", "Unit", "Component",
                         "Accessory", "Equipment"};
        
        Random random = new Random(threadIndex); // Use thread index as seed for reproducibility
        List<Product> products = new ArrayList<>();
        int batchSize = 1000;
        
        System.out.printf("Thread %d: Processing products %d to %d%n", threadIndex, startIndex, endIndex - 1);
        
        for (int i = startIndex; i < endIndex; i++) {
            String category = categories[random.nextInt(categories.length)];
            String adjective = adjectives[random.nextInt(adjectives.length)];
            String noun = nouns[random.nextInt(nouns.length)];
            String name = adjective + " " + noun + " " + (i + 1);
            
            BigDecimal price = BigDecimal.valueOf(random.nextDouble() * 999 + 1).setScale(2, BigDecimal.ROUND_HALF_UP);
            int quantity = random.nextInt(1000) + 1;
            
            Product product = new Product(name, category, price, quantity);
            products.add(product);
            
            // Batch insert every 1000 products
            if (products.size() >= batchSize) {
                if (!productRepository.bulkInsertProducts(products)) {
                    System.err.printf("Thread %d: Failed to insert batch at product %d%n", threadIndex, i + 1);
                    return false;
                }
                products.clear();
                
                // Progress reporting every 10,000 products
                if ((i + 1) % 10000 == 0) {
                    System.out.printf("Thread %d: Inserted %d products...%n", threadIndex, i + 1 - startIndex);
                }
            }
        }
        
        // Insert remaining products
        if (!products.isEmpty()) {
            if (!productRepository.bulkInsertProducts(products)) {
                System.err.printf("Thread %d: Failed to insert final batch%n", threadIndex);
                return false;
            }
        }
        
        System.out.printf("Thread %d: Completed insertion of %d products%n", threadIndex, endIndex - startIndex);
        return true;
    }


    public void read10MillionProducts() {
        System.out.println("Reading 10 million products with pagination...");
        
        long startTime = System.currentTimeMillis();
        int totalCount = productService.getTotalProductCount();
        
        if (totalCount == 0) {
            System.out.println("No products found in database.");
            return;
        }
        
        System.out.printf("Total products in database: %,d%n", totalCount);
        
        int pageSize = 1000;
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int displayedProducts = 0;
        
        System.out.printf("Reading products in pages of %d...%n", pageSize);
        
        for (int page = 1; page <= totalPages && displayedProducts < 10_000_000; page++) {
            List<Product> products = productService.getProductsWithPagination(page, pageSize);
            
            if (products.isEmpty()) {
                break;
            }
            
            displayedProducts += products.size();
            
            // Display progress every 100 pages (100,000 products)
            if (page % 100 == 0) {
                System.out.printf("Read %,d products (Page %d/%d)...%n", displayedProducts, page, totalPages);
            }
            
            // Stop if we've read 10 million products
            if (displayedProducts >= 10_000_000) {
                break;
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("Successfully read %,d products%n", Math.min(displayedProducts, 10_000_000));
        System.out.printf("Total time: %.2f seconds%n", duration / 1000.0);
        System.out.printf("Average rate: %.0f products/second%n", Math.min(displayedProducts, 10_000_000) / (duration / 1000.0));
    }


    public void performanceTestSearch() {
        System.out.println("Running search performance tests...");
        
        String[] searchTerms = {"Premium", "Standard", "Professional", "Modern", "Classic"};
        String[] categories = {"Electronics", "Clothing", "Books", "Sports", "Health"};
        
        // Test name search performance
        System.out.println("\nTesting product name search performance:");
        for (String term : searchTerms) {
            long startTime = System.currentTimeMillis();
            List<Product> results = productService.searchProductsByName(term);
            long endTime = System.currentTimeMillis();
            
            System.out.printf("Search '%s': %,d results in %d ms%n", 
                             term, results.size(), endTime - startTime);
        }
        
        // Test category search performance
        System.out.println("\nTesting category search performance:");
        for (String category : categories) {
            long startTime = System.currentTimeMillis();
            List<Product> results = productService.searchProductsByCategory(category);
            long endTime = System.currentTimeMillis();
            
            System.out.printf("Category '%s': %,d results in %d ms%n", 
                             category, results.size(), endTime - startTime);
        }
    }

}

