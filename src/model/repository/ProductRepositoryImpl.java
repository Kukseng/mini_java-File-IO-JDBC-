package model.repository;

import model.entities.Product;
import model.utils.DatabaseConfigure;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public List<Product> getAllProducts(String category) {
        List<Product> products = new ArrayList<>();
        String sql;
        PreparedStatement stmt = null;

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            if (category == null || category.trim().isEmpty()) {
                sql = "SELECT * FROM products WHERE is_deleted = FALSE ORDER BY category, p_name";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT * FROM products WHERE category ILIKE ? AND is_deleted = FALSE ORDER BY p_name";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, "%" + category.trim() + "%");
            }

            System.out.println("[ProductRepositoryImpl] Executing query for products" +
                    (category != null ? " in category: " + category : ""));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
            System.out.println("[ProductRepositoryImpl] Found " + products.size() + " products");

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error retrieving products: " + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("[ProductRepositoryImpl] Error closing statement: " + e.getMessage());
                }
            }
        }
        return products;
    }

    @Override
    public List<Product> searchProductsByName(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE p_name ILIKE ? AND is_deleted = FALSE ORDER BY p_name";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            System.out.println("[ProductRepositoryImpl] Executing search by name: " + searchTerm);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error searching products: " + e.getMessage());
        }
        return products;
    }

    @Override
    public List<Product> searchProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category ILIKE ? AND is_deleted = FALSE ORDER BY p_name";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + category + "%");
            System.out.println("[ProductRepositoryImpl] Executing search by category: " + category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error searching products by category: " + e.getMessage());
        }
        return products;
    }

    @Override
    public Product getProductByUuid(String uuid) {
        String sql = "SELECT * FROM products WHERE p_uuid = ? AND is_deleted = FALSE";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid);
            System.out.println("[ProductRepositoryImpl] Executing query for product UUID: " + uuid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error retrieving product by UUID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products WHERE is_deleted = FALSE ORDER BY category";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("[ProductRepositoryImpl] Executing query for all categories");
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error retrieving categories: " + e.getMessage());
        }
        return categories;
    }

    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (p_name, category, price, qty, p_uuid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getUuid());

            System.out.println("[ProductRepositoryImpl] Adding product: " + product.getName());
            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error adding product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProductQuantity(String uuid, int newQuantity) {
        String sql = "UPDATE products SET qty = ? WHERE p_uuid = ? AND is_deleted = FALSE";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, uuid);
            System.out.println("[ProductRepositoryImpl] Updating quantity for product UUID: " + uuid);
            int result = stmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error updating product quantity: " + e.getMessage());
            return false;
        }
    }

    public List<Product> getProductsWithPagination(int offset, int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_deleted = FALSE ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            System.out.println("[ProductRepositoryImpl] Executing paginated query: limit=" + limit + ", offset=" + offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error retrieving products with pagination: " + e.getMessage());
        }
        return products;
    }

    public int getTotalProductCount() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_deleted = FALSE";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("[ProductRepositoryImpl] Executing product count query");
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error getting product count: " + e.getMessage());
        }
        return 0;
    }

    public boolean bulkInsertProducts(List<Product> products) {
        String sql = "INSERT INTO products (p_name, category, price, qty, p_uuid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Product product : products) {
                    stmt.setString(1, product.getName());
                    stmt.setString(2, product.getCategory());
                    stmt.setBigDecimal(3, product.getPrice());
                    stmt.setInt(4, product.getQuantity());
                    stmt.setString(5, product.getUuid());
                    stmt.addBatch();
                }

                System.out.println("[ProductRepositoryImpl] Executing bulk insert for " + products.size() + " products");
                stmt.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("[ProductRepositoryImpl] Error during bulk insert: " + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("[ProductRepositoryImpl] Error bulk inserting products: " + e.getMessage());
            return false;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setUuid(rs.getString("p_uuid"));
        product.setName(rs.getString("p_name"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setQuantity(rs.getInt("qty"));
        product.setIsDeleted(rs.getBoolean("is_deleted"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            product.setCreatedAt(timestamp.toLocalDateTime());
        }

        return product;
    }
}