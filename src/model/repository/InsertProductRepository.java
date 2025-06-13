package model.repository;


import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class InsertProductRepository {

    private final Connection connection;

    public InsertProductRepository(Connection connection) {
        this.connection = connection;
    }

    public long insert10Million() throws SQLException, IOException, InterruptedException {
        long startGeneration = System.currentTimeMillis();

        CopyManager copyManager = new CopyManager((BaseConnection) connection);
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);

        Thread writerThread = new Thread(() -> {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
                for (int i = 1; i <= 10_000_000; i++) {
                    String row = String.format(
                            "Product-%d,General,%.2f,%d,false,%s\n",
                            i,
                            Math.random() * 100,
                            (int) (Math.random() * 1000),
                            UUID.randomUUID()
                    );
                    writer.write(row);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error writing CSV data: " + e.getMessage(), e);
            }
        });

        writerThread.start();

        long startCopy = System.currentTimeMillis();
        String copySql = "COPY products (p_name, category, p_price, p_quantity, is_deleted, p_uuid) FROM STDIN WITH (FORMAT csv)";
        try {
            copyManager.copyIn(copySql, in);
        } catch (SQLException e) {
            throw new SQLException("COPY command failed: " + e.getMessage(), e);
        }

        writerThread.join();
        long endCopy = System.currentTimeMillis();

        System.out.println("Data generation took: " + (startCopy - startGeneration) + " ms");
        System.out.println("COPY operation took: " + (endCopy - startCopy) + " ms");

        return endCopy - startGeneration;
    }

    public long read10Million() throws SQLException {
        String sql = "SELECT * FROM products";
        Statement stmt = null;
        ResultSet rs = null;

        long start = System.currentTimeMillis();

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);

            int count = 0;
            while (rs.next()) {
                count++;
                if (count % 1_000_000 == 0) {
                    System.out.println("Read rows: " + count);
                }
            }

            System.out.println("Total rows read: " + count);
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }

        return System.currentTimeMillis() - start;
    }
}