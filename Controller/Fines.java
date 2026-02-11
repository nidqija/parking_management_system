package Controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import Data.Sqlite;
import java.util.ArrayList;
import java.util.List;


public class Fines {

    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    String license_plate;
    String fine_type;
    double amount;
    String status;
    
    public Fines(String license_plate, String fine_type, double amount, String status) {
        // Constructor implementation
        this.license_plate = license_plate;
        this.fine_type = fine_type;
        this.amount = amount;
        this.status = status;
            
    }

    public String getFineID() {return fine_type;}
    public String getVehiclePlate() {return license_plate;}
    public double getAmount() {return amount;}
    public String getStatus() {return status;}


    public static List<Fines> getFinesReportList() {
        List<Fines> finesList = new ArrayList<>();
        String sql = "SELECT license_plate, violation_type, amount, status FROM Fines_Ledger";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String license_plate = rs.getString("license_plate");
                String fine_type = rs.getString("violation_type");
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");

                Fines fine = new Fines(license_plate, fine_type, amount, status);
                finesList.add(fine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finesList;
    }
}
