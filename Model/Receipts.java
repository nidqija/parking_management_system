package Model;

import Data.Sqlite;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Receipts {
    
    public Receipts() {
    }


    public void insertReceipt(String ticketNumber, String licensePlate, String spotId, 
                              String entryTime, String exitTime, int durationHours, 
                              double parkingFee, String paymentMethod) {
        
        String sql = "INSERT INTO Receipts (ticket_number, license_plate, spot_id, " +
                     "entry_time, exit_time, duration_hours, parking_fee, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

   
        Sqlite db = new Sqlite(); 
        try (Connection conn = db.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ticketNumber);
            pstmt.setString(2, licensePlate);
            pstmt.setString(3, spotId);
            pstmt.setString(4, entryTime);
            pstmt.setString(5, exitTime);
            pstmt.setInt(6, durationHours);
            pstmt.setDouble(7, parkingFee);
            pstmt.setString(8, paymentMethod);

            pstmt.executeUpdate();
            System.out.println("Receipt generated successfully for Ticket: " + ticketNumber);
            
        } catch (Exception e) {
            System.err.println("Error inserting receipt: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
