import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Check all COMPACT spots to see if they might be misconfigured
 */
public class CheckAllSpots {
    
    public static void main(String[] args) {
        System.out.println("=== Checking All Spot Configurations ===\n");
        
        try (Connection conn = new Data.Sqlite().connect()) {
            
            // Get spot configuration by type
            String[] types = {"COMPACT", "REGULAR", "HANDICAPPED", "RESERVED"};
            
            for (String type : types) {
                String sql = "SELECT COUNT(*) as count, MIN(hourly_rate) as min_rate, " +
                            "MAX(hourly_rate) as max_rate FROM Parking_Spots WHERE spot_type = ?";
                
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, type);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    double minRate = rs.getDouble("min_rate");
                    double maxRate = rs.getDouble("max_rate");
                    
                    System.out.printf("%-12s: %2d spots | Min Rate: RM %.2f | Max Rate: RM %.2f", 
                                     type, count, minRate, maxRate);
                    
                    if (minRate != maxRate) {
                        System.out.print(" ⚠️  RATE MISMATCH!");
                    }
                    System.out.println();
                }
            }
            
            // Show expected rates
            System.out.println("\n=== Expected Rates ===");
            System.out.println("COMPACT      : RM 2.00/hour");
            System.out.println("REGULAR      : RM 5.00/hour");
            System.out.println("HANDICAPPED  : RM 2.00/hour");
            System.out.println("RESERVED     : RM 10.00/hour");
            
            // Check for any spots with wrong rates
            System.out.println("\n=== Spots with Incorrect Rates ===");
            String sql = "SELECT spot_id, spot_type, hourly_rate FROM Parking_Spots WHERE " +
                        "(spot_type = 'COMPACT' AND hourly_rate != 2.0) OR " +
                        "(spot_type = 'REGULAR' AND hourly_rate != 5.0) OR " +
                        "(spot_type = 'HANDICAPPED' AND hourly_rate != 2.0) OR " +
                        "(spot_type = 'RESERVED' AND hourly_rate != 10.0)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            boolean foundIssues = false;
            while (rs.next()) {
                foundIssues = true;
                System.out.printf("❌ %s (%s) has rate RM %.2f%n",
                                 rs.getString("spot_id"),
                                 rs.getString("spot_type"),
                                 rs.getDouble("hourly_rate"));
            }
            
            if (!foundIssues) {
                System.out.println("✅ All spots have correct rates!");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
