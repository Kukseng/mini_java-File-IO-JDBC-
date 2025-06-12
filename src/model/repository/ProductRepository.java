package model.repository;

import model.entities.Product;
import model.utils.DatabaseConfigure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository implements Repository<Product, String> {

    @Override
    public Product save(Product product) {
        try (Connection con = DatabaseConfigure.getDatabaseConnection()) {
            String sql = """
                INSERT INTO products (p_uuid, p_name, category, price, qty, is_deleted)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            PreparedStatement pre = con.prepareStatement(sql);
            pre.setString(1, product.getP_uuid());
            pre.setString(2, product.getP_name());
            pre.setString(3, product.getCategory());
            pre.setDouble(4, product.getPrice());
            pre.setInt(5, product.getQty());
            pre.setBoolean(6, product.is_deleted());

            int rowAffected = pre.executeUpdate();

            if (rowAffected > 0) {
                System.out.println(" Product inserted successfully.");
                return product;
            }

        } catch (Exception e) {
            System.err.println(" Error inserting product: " + e.getMessage());
        }
        return product;
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_deleted = false";

        try (Connection con = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement pre = con.prepareStatement(sql);
             ResultSet rs = pre.executeQuery()) {

            while (rs.next()) {
                Product product = Product.builder()
                        .id(rs.getInt("id"))
                        .p_uuid(rs.getString("p_uuid"))
                        .p_name(rs.getString("p_name"))
                        .category(rs.getString("category"))
                        .price(rs.getDouble("price"))
                        .qty(rs.getInt("qty"))
                        .is_deleted(rs.getBoolean("is_deleted"))
                        .build();

                products.add(product);
            }

        } catch (Exception e) {
            System.err.println(" Error fetching products: " + e.getMessage());
        }

        return products;
    }

    @Override
    public String delete(String uuid) {
        String sql = "UPDATE products SET is_deleted = true WHERE p_uuid = ?";
        try (Connection con = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement pre = con.prepareStatement(sql)) {

            pre.setString(1, uuid);
            int rowAffected = pre.executeUpdate();
            if (rowAffected > 0) {
                System.out.println(" Product marked as deleted.");
                return uuid;
            }

        } catch (Exception e) {
            System.err.println(" Error marking product as deleted: " + e.getMessage());
        }

        return null;
    }

    //  Search method based on name and category
    public List<Product> searchProducts(String name, String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE p_name ILIKE ? AND category ILIKE ? AND is_deleted = false";

        try (Connection con = DatabaseConfigure.getDatabaseConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, name + "%");
            pstmt.setString(2, category + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Product product = Product.builder()
                        .id(rs.getInt("id"))
                        .p_uuid(rs.getString("p_uuid"))
                        .p_name(rs.getString("p_name"))
                        .category(rs.getString("category"))
                        .price(rs.getDouble("price"))
                        .qty(rs.getInt("qty"))
                        .is_deleted(rs.getBoolean("is_deleted"))
                        .build();

                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println(" Error searching products: " + e.getMessage());
        }

        return products;
    }
}
