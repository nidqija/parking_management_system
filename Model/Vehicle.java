package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Vehicle {

    public enum VehicleType {
        CAR, MOTORCYCLE, TRUCK, HANDICAPPED  //add more types as needed 
    }

    private String plateNumber;
    private VehicleType type;
    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";

    public Vehicle(String plateNumber, VehicleType type) {
        this.plateNumber = plateNumber;
        this.type = type;

        registerInDB();
    }

    public String getPlateNumber() { return plateNumber; }
    public VehicleType getType() { return type; }

    private void registerInDB() {
        String sql = "INSERT OR IGNORE INTO Vehicles(license_plate, vehicle_type) VALUES(?,?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.plateNumber);
            pstmt.setString(2, this.type.toString()); 
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error registering vehicle: " + e.getMessage());
        }
    }
}