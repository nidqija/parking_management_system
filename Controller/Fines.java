package Controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public Fines() {
        // Default constructor
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


        public List<Fines> getUnpaidFines() {
            List<Fines> fines = new ArrayList<>();
            String query = "SELECT license_plate, violation_type, amount, status FROM Fines_Ledger WHERE status = 'UNPAID'";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    String license_plate = rs.getString("license_plate");
                    String fine_type = rs.getString("violation_type");
                    double amount = rs.getDouble("amount");
                    String status = rs.getString("status");

                    Fines fine = new Fines(license_plate, fine_type, amount, status);
                    fines.add(fine);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fines;
    }


    public void checkAndRecordFine(String plate , String violationType, double fineAmount , String ticketRef) {

        if (fineAmount <= 0) {
            return; 
        }

        
        String insertFineSQL = "INSERT INTO Fines_Ledger (license_plate, violation_type, amount, status, created_at, ticket_ref) " +
                           "VALUES (?, ?, ?, ?, datetime('now'), ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertFineSQL)) {

            pstmt.setString(1, plate);
            pstmt.setString(2, violationType);
            pstmt.setDouble(3, fineAmount);
            pstmt.setString(4, "UNPAID");
            pstmt.setString(5, ticketRef);
            pstmt.executeUpdate();

            System.out.println("Fine recorded for vehicle: " + plate);

        } catch (Exception e) {
            System.err.println("Error recording fine: " + e.getMessage());
        }
    }


    public double getUnpaidLedgerTotal(String plate) {
    double total = 0.0;
    String sql = "SELECT SUM(amount) FROM Fines_Ledger WHERE license_plate = ? AND status = 'UNPAID'";
    try (Connection conn = DriverManager.getConnection(DB_URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, plate);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            total = rs.getDouble(1);
        }
    } catch (Exception e) {
        System.err.println("Error calculating unpaid fines total: " + e.getMessage());
    }
    return total;
}

}
