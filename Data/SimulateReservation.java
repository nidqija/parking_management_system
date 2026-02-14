package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Simulates reservation from external system
public class SimulateReservation {

    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        // --- Reservation Details ---
        String plate = "SIM1234";
        String vehicleType = "CAR";
        String spotId = "F1-R1-S5"; // A COMPACT spot on Floor 1
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);

        System.out.println("=== External Reservation Simulator ===");
        System.out.println("Plate:      " + plate);
        System.out.println("Spot:       " + spotId);
        System.out.println("From:       " + startTime.format(FORMATTER));
        System.out.println("To:         " + endTime.format(FORMATTER));
        System.out.println();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // Step 1: Register the vehicle (if not already registered)
            String insertVehicle = "INSERT OR IGNORE INTO Vehicles (license_plate, vehicle_type) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertVehicle)) {
                pstmt.setString(1, plate);
                pstmt.setString(2, vehicleType);
                pstmt.executeUpdate();
                System.out.println("[OK] Vehicle registered: " + plate);
            }

            // Step 2: Insert the reservation
            String insertReservation = "INSERT INTO Reservations (license_plate, spot_id, start_time, end_time, status) "
                    +
                    "VALUES (?, ?, ?, ?, 'ACTIVE')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertReservation)) {
                pstmt.setString(1, plate);
                pstmt.setString(2, spotId);
                pstmt.setString(3, startTime.format(FORMATTER));
                pstmt.setString(4, endTime.format(FORMATTER));
                pstmt.executeUpdate();
                System.out.println("[OK] Reservation created for spot " + spotId);
            }

            // Step 3: Update Parking_Spots table to reflect the reservation
            String updateSpot = "UPDATE Parking_Spots SET reserved_for_plate = ?, status = 'RESERVED', spot_type = 'RESERVED' WHERE spot_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSpot)) {
                pstmt.setString(1, plate);
                pstmt.setString(2, spotId);
                pstmt.executeUpdate();
                System.out.println("[OK] Parking_Spots updated: " + spotId + " reserved for " + plate);
            }

            System.out.println();
            System.out.println("=== Reservation Confirmed ===");
            System.out.println("Customer " + plate + " has reserved spot " + spotId);
            System.out.println("Valid from " + startTime.format(FORMATTER) + " to " + endTime.format(FORMATTER));

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
