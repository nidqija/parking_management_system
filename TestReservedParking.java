import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Test class to verify Reserved parking spots are configured with RM 10/hour rate
 */
public class TestReservedParking {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Reserved Parking Configuration ===\n");
        
        try (Connection conn = new Data.Sqlite().connect()) {
            Statement stmt = conn.createStatement();
            
            // Query to get all RESERVED spots and their rates
            String sql = "SELECT spot_id, spot_type, hourly_rate, reserved_for_plate " +
                        "FROM Parking_Spots WHERE spot_type = 'RESERVED' " +
                        "ORDER BY spot_id";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("Reserved Parking Spots Configuration:");
            System.out.println("----------------------------------------");
            System.out.printf("%-12s %-12s %-12s %-15s%n", 
                            "Spot ID", "Type", "Rate (RM/hr)", "Reserved For");
            System.out.println("----------------------------------------");
            
            int count = 0;
            boolean allCorrect = true;
            
            while (rs.next()) {
                String spotId = rs.getString("spot_id");
                String type = rs.getString("spot_type");
                double rate = rs.getDouble("hourly_rate");
                String reservedFor = rs.getString("reserved_for_plate");
                
                System.out.printf("%-12s %-12s RM %-9.2f %-15s%n", 
                                spotId, type, rate, 
                                (reservedFor != null ? reservedFor : "Available"));
                
                count++;
                
                // Verify rate is 10.0
                if (rate != 10.0) {
                    allCorrect = false;
                    System.out.println("  ⚠️  ERROR: Rate should be RM 10.00!");
                }
            }
            
            System.out.println("----------------------------------------");
            System.out.println("Total Reserved Spots: " + count);
            
            if (allCorrect && count > 0) {
                System.out.println("\n✅ All reserved parking spots are correctly configured at RM 10/hour!");
            } else if (count == 0) {
                System.out.println("\n⚠️  No reserved parking spots found. Run Sqlite.java to initialize database.");
            } else {
                System.out.println("\n❌ Some spots have incorrect rates!");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
