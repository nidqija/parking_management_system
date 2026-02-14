import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Migration script to add reservation_id column to Tickets table
 * Run this file ONCE if you already have an existing database
 */
public class MigrateAddReservationId {
    
    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    
    public static void main(String[] args) {
        System.out.println("=== Starting Migration: Add reservation_id to Tickets ===");
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Check if column already exists by trying to add it
            // SQLite doesn't have ADD COLUMN IF NOT EXISTS, so we'll handle the exception
            try {
                String alterSQL = "ALTER TABLE Tickets ADD COLUMN reservation_id INTEGER " +
                                  "REFERENCES Reservations(reservation_id)";
                stmt.execute(alterSQL);
                System.out.println("[SUCCESS] Column 'reservation_id' added to Tickets table.");
            } catch (Exception e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("[INFO] Column 'reservation_id' already exists. Skipping...");
                } else {
                    throw e;
                }
            }
            
            System.out.println("=== Migration Completed Successfully ===");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
