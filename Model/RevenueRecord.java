package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RevenueRecord {
    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    public String ticketNumber, licensePlate, spotType, floorName, entryTime, exitTime, paymentMethod;
    public int durationHours;
    public double parkingFee, fineAmount, totalRevenue;

    public RevenueRecord(String tNum, String plate, String sType, String floor, String entry, 
                         String exit, int hours, double fee, double fine, double total, String method) {
        this.ticketNumber = tNum;
        this.licensePlate = plate;
        this.spotType = sType;
        this.floorName = floor;
        this.entryTime = entry;
        this.exitTime = exit;
        this.durationHours = hours;
        this.parkingFee = fee;
        this.fineAmount = fine;
        this.totalRevenue = total;
        this.paymentMethod = method;
    }



    public static List<RevenueRecord> getRevenueRecords() {
    List<RevenueRecord> revenueList = new ArrayList<>();


    String sql = "SELECT " +
            "t.ticket_number, " +
            "t.license_plate, " +
            "s.spot_type, " +
            "f.floor_name, " +
            "t.entry_time, " +
            "t.exit_time, " +
            "t.duration_hours, " +
            "t.parking_fee, " +
            "IFNULL(fl.amount, 0) AS fine_amount, " +
            "(t.parking_fee + IFNULL(fl.amount, 0)) AS total_revenue, " +
            "t.payment_method " +
            "FROM Tickets t " +
            "JOIN Parking_Spots s ON t.spot_id = s.spot_id " +
            "JOIN Floors f ON s.floor_id = f.floor_id " +
            "LEFT JOIN Fines_Ledger fl ON t.ticket_number = fl.ticket_ref " +
            "WHERE t.payment_status = 'PAID';";

    // Using your existing 'url' from the Sqlite class
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            // Mapping the ResultSet to our RevenueRecord object
            RevenueRecord record = new RevenueRecord(
                rs.getString("ticket_number"),
                rs.getString("license_plate"),
                rs.getString("spot_type"),
                rs.getString("floor_name"),
                rs.getString("entry_time"),
                rs.getString("exit_time"),
                rs.getInt("duration_hours"),
                rs.getDouble("parking_fee"),
                rs.getDouble("fine_amount"),
                rs.getDouble("total_revenue"),
                rs.getString("payment_method")
            );
            revenueList.add(record);
        }
    } catch (SQLException e) {
        System.err.println("Database Error: " + e.getMessage());
    }
    
    return revenueList;
}
}