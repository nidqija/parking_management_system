package Model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Data.Sqlite;

public class Reservation {


    public void completeReservation(String plate, String spot_id) {
        String sql = "UPDATE Reservations SET status = 'completed' WHERE spot_id = ? AND license_plate = ?";
        Sqlite db = new Sqlite();
        try (Connection conn = db.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spot_id);
            pstmt.setString(2, plate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void getReservedVehicle(String spot_ID) {
        String sql = "SELECT license_plate, spot_id FROM Reservations WHERE status = 'active' AND spot_id = ?";
        Sqlite db = new Sqlite();
        try (Connection conn = db.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spot_ID);
            pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        //for if a vehicle tries to park in a reserved spot without a reservation, or with the wrong plate
        public void checkReservationViolations() {
    // 1. Get all occupied reserved spots
    String querySpots = "SELECT spot_id, current_vehicle_plate, reserved_for_plate " +
                        "FROM Parking_Spots " +
                        "WHERE spot_type = 'RESERVED' AND status = 'OCCUPIED'";

    // 2. Prepare the update statement with a placeholder (?) for the license plate
    String updateTicket = "UPDATE Tickets " +
                          "SET reserved_violation = 1 " +
                          "WHERE license_plate = ? AND payment_status = 'UNPAID'";

    Sqlite db = new Sqlite();

    try (Connection conn = db.connect();
         PreparedStatement pstmtSelect = conn.prepareStatement(querySpots);
         ResultSet rs = pstmtSelect.executeQuery()) {

        // Loop through every occupied reserved spot
        while (rs.next()) {
            String spotId = rs.getString("spot_id");
            String currentPlate = rs.getString("current_vehicle_plate");
            String reservedPlate = rs.getString("reserved_for_plate");

            // Check if a vehicle is there, and if it DOES NOT match the reserved plate.
            // (If reservedPlate is null, equalsIgnoreCase(null) returns false, so !false becomes true = Violation)
            if (currentPlate != null && !currentPlate.equalsIgnoreCase(reservedPlate)) {
                
                // Vehicle doesn't match! Execute the update for this specific vehicle.
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateTicket)) {
                   
                    pstmtUpdate.setString(1, currentPlate); 
                    
                    int rowsAffected = pstmtUpdate.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        System.out.println("Violation Flagged: Vehicle " + currentPlate + 
                                           " is illegally parked in reserved spot " + spotId);
                    }
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error checking violations: " + e.getMessage());
    }
}
}
