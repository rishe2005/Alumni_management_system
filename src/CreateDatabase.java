package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:db/alumni.db")) {
            String createTables = """
                CREATE TABLE IF NOT EXISTS alumni (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name VARCHAR(100) NOT NULL,
                    branch VARCHAR(50) NOT NULL,
                    year_of_graduation INT NOT NULL,
                    phone_number VARCHAR(15) NOT NULL,
                    address TEXT NOT NULL
                );
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTables);
                System.out.println("Alumni database created successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
