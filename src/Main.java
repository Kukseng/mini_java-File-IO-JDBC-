import controller.InsertProductController;
import controller.ProductController;
import model.repository.InsertProductRepository;
import model.service.InsertProductService;
import model.service.impl.InsertProductServiceImpl;
import model.utils.DatabaseConfigure;
import view.Header;
import view.Register;
import view.TimeView;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        Connection conn = DatabaseConfigure.getDatabaseConnection();
        if (conn == null) {
            System.err.println("Failed to establish database connection");
            return;
        }

        try {
            // Apply dynamic PostgreSQL optimizations
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET maintenance_work_mem = '512MB'");
                stmt.execute("SET synchronous_commit = off");
                stmt.execute("SET wal_compression = off");
                stmt.execute("ALTER TABLE products DISABLE TRIGGER ALL");
                stmt.execute("DROP INDEX IF EXISTS idx_product_name");
            }

            InsertProductRepository repository = new InsertProductRepository(conn);
            InsertProductService productService = new InsertProductServiceImpl(repository);
            TimeView timeView = new TimeView();
            InsertProductController controller = new InsertProductController(productService, timeView);

            controller.runBenchmark();

            // Restore settings and indexes
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_product_name ON products(p_name)");
                stmt.execute("ALTER TABLE products ENABLE TRIGGER ALL");
                stmt.execute("RESET maintenance_work_mem");
                stmt.execute("RESET synchronous_commit");
                stmt.execute("RESET wal_compression");
            }
        } catch (SQLException e) {
            System.err.println("Database optimization error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }

    }
}