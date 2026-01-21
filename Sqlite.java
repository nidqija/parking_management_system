import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlite {
    public static void main(String[] args) {
        // This will create 'sample.db' in your project folder
        String url = "jdbc:sqlite:Data/sample.db";

        // 1. Load the driver (Helps prevent the "No suitable driver" error)
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite Driver not found. Check your pom.xml!");
            return;
        }

        // 2. Establish connection and create a table
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connection to SQLite has been established.");
                
                // Create a sample table
                String sql = "CREATE TABLE IF NOT EXISTS students (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "name TEXT NOT NULL);";
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Table 'students' is ready.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}