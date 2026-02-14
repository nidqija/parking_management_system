import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Diagnostic tool to check VIP vehicle parking and rates
 */
public class DiagnoseVIPRate {
    
    public static void main(String[] args) {
        System.out.println("=== Diagnosing VIP Rate Issue ===\n");
        
        try (Connection conn = new Data.Sqlite().connect()) {
            
            // Find all vehicles with VIP in their plate
            String sql = "SELECT t.ticket_number, t.license_plate, t.spot_id, t.entry_time, " +
                        "s.spot_type, s.hourly_rate, s.reserved_for_plate " +
                        "FROM Tickets t " +
                        "JOIN Parking_Spots s ON t.spot_id = s.spot_id " +
                        "WHERE t.license_plate LIKE '%VIP%' AND t.exit_time IS NULL";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Active VIP Tickets:");
            System.out.println("----------------------------------------------------------");
            
            while (rs.next()) {
                String plate = rs.getString("license_plate");
                String spot = rs.getString("spot_id");
                String type = rs.getString("spot_type");
                double rate = rs.getDouble("hourly_rate");
                String reserved = rs.getString("reserved_for_plate");
                
                System.out.println("Plate: " + plate);
                System.out.println("  Spot: " + spot);
                System.out.println("  Type: " + type);
                System.out.println("  Rate: RM " + rate + "/hour");
                System.out.println("  Reserved For: " + (reserved != null ? reserved : "N/A"));
                
                if (type.equals("RESERVED") && rate != 10.0) {
                    System.out.println("  ⚠️  ERROR: RESERVED spot should have RM 10/hour rate!");
                }
                System.out.println();
            }
            
            // Check if VIP-9999 exists in database
            System.out.println("\n=== Checking VIP-9999 Spot Configuration ===");
            String sql2 = "SELECT spot_id, spot_type, hourly_rate FROM Parking_Spots " +
                         "WHERE current_vehicle_plate = 'VIP-9999'";
            pstmt = conn.prepareStatement(sql2);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("VIP-9999 is in spot: " + rs.getString("spot_id"));
                System.out.println("Spot type: " + rs.getString("spot_type"));
                System.out.println("Rate: RM " + rs.getDouble("hourly_rate") + "/hour");
            } else {
                System.out.println("VIP-9999 not currently parked");
            }
            
            // Check all vehicles with plates like VIP-2%
            System.out.println("\n=== All VIP-2% Vehicles ===");
            String sql3 = "SELECT license_plate FROM Vehicles WHERE license_plate LIKE 'VIP-2%'";
            pstmt = conn.prepareStatement(sql3);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                System.out.println("Found: " + rs.getString("license_plate"));
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
