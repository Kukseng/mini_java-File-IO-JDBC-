package model.repository;

import model.entities.Product;
import model.utils.DatabaseConfigure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_deleted = FALSE ORDER BY category, p_name";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving products: " + e.getMessage());
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
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
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
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error searching products by category: " + e.getMessage());
        }
        return products;
    }

    @Override
    public Product getProductByUuid(String uuid) {
        String sql = "SELECT * FROM products WHERE p_uuid = ? AND is_deleted = FALSE";

        try (Connection conn = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving product by UUID: " + e.getMessage());
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

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
        }
        return categories;
    }


    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {

        Product product = new Product();
        product.setUuid(rs.getString("p_uuid"));
        product.setName(rs.getString("p_name"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getBigDecimal("price"));

        return product;
    }
}

