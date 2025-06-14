package view;

import model.repository.InsertProductRepository;
import model.utils.DatabaseConfigure;

import java.sql.Connection;
import java.sql.DriverManager;

public class TimeView {
    public static void main(String[] args) {
        Connection con = DatabaseConfigure.getDatabaseConnection();
        try (con) {
            InsertProductRepository repo = new InsertProductRepository(con);

            System.out.println("Running Insert Benchmark...");
            long insertTime = repo.insert10Million();
            System.out.println("Insert 10 million took: " + insertTime + " ms");

            System.out.println("Running Read Benchmark...");
            long readTime = repo.read10Million();
            System.out.println("Read 10 million took: " + readTime + " ms");

        } catch (Exception e) {
            System.out.println("Benchmark Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
