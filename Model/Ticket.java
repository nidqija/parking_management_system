package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


//When this constructor is called, it performs the insertion to the database with 
// Ticket ticket = new Ticket(vehicle, spotID), FIND at entry panel line 124
public class Ticket {
    private String ticketID;
    private Vehicle vehicle;
    private String spotID;
    private LocalDateTime entryTime;
    private boolean reservedViolation;

          

    // 1. Database FORMAT, so that easy to query later, this is what will be stored in DB
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 2. REQUIREMENT FORMAT, this is what will be displayed on ticket
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");


     private void detectReservedViolation() {
        reservedViolation = false; // default to false

          String sql =
            "SELECT r.license_plate " +
            "FROM Parking_Spots s " +
            "LEFT JOIN Reservations r ON s.spot_id = r.spot_id " +
            "AND datetime('now') BETWEEN r.start_time AND r.end_time " +
            "WHERE s.spot_id = ? AND s.spot_type = 'RESERVED'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.spotID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String reservedPlate = rs.getString("license_plate");

                if (reservedPlate == null ||
                    !reservedPlate.equals(vehicle.getPlateNumber())) {
                    reservedViolation = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasReservedViolation() {
        return reservedViolation;
    }

    

    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    public Ticket(Vehicle vehicle, String spotID) {
        this.vehicle = vehicle;
        this.spotID = spotID;
        this.entryTime = LocalDateTime.now().withNano(0);
        this.ticketID = generateTicketID();
        detectReservedViolation();

        saveToDB();
    }

    

    public Ticket(String ticketID, Vehicle vehicle, String spotID, LocalDateTime entryTime) {
    this.ticketID = ticketID;
    this.vehicle = vehicle;
    this.spotID = spotID;
    this.entryTime = entryTime;
    detectReservedViolation();
}

    private String generateTicketID() {
        // Requirement: T-PLATE-TIMESTAMP
        return "T-" + vehicle.getPlateNumber() + "-" + entryTime.format(ID_FORMATTER);
    }

  public String getTicketDetails() {
        return "---------------------------------\n" +
               " TICKET ID : " + ticketID + "\n" +
               " SPOT NO   : " + spotID + "\n" +
               " VEHICLE   : " + vehicle.getPlateNumber() + "\n" +
               " ENTRY TIME: " + entryTime.format(DB_FORMATTER) + "\n" +
               "---------------------------------";
    }
  

    public String getTicketID() { return ticketID; }
    public String getSpotID() { return spotID; }
    public String getPlateNumber() { return vehicle.getPlateNumber(); }
    public String getEntryTimeStr() { return entryTime.toString(); }

    private void saveToDB() {
        String insertTicketSQL = "INSERT INTO Tickets(ticket_number, license_plate, spot_id, entry_time, payment_status, reserved_violation) VALUES(?,?,?,?,?,?)";
        String updateSpotSQL = "UPDATE Parking_Spots SET status = 'OCCUPIED', current_vehicle_plate = ? WHERE spot_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false); // Transaction

            // 1. Insert Ticket
            try (PreparedStatement pstmt = conn.prepareStatement(insertTicketSQL)) {
                pstmt.setString(1, this.ticketID);
                pstmt.setString(2, this.vehicle.getPlateNumber());
                pstmt.setString(3, this.spotID);
                pstmt.setString(4, this.entryTime.format(DB_FORMATTER)); 
                pstmt.setString(5, "UNPAID");
                //violation status (1 for true, 0 for false)
                pstmt.setInt(6, reservedViolation ? 1 : 0);
                pstmt.executeUpdate();
            }

            // 2. Update Spot Status
            try (PreparedStatement pstmt = conn.prepareStatement(updateSpotSQL)) {
                pstmt.setString(1, this.vehicle.getPlateNumber());
                pstmt.setString(2, this.spotID);
                pstmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Ticket generated and saved: " + this.ticketID);

        } catch (SQLException e) {
            System.err.println("Error saving ticket: " + e.getMessage());
        }
    }


    public static List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_number, license_plate, spot_id, entry_time FROM Tickets";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String ticketID = rs.getString("ticket_number");
                String plateNumber = rs.getString("license_plate");
                String spotID = rs.getString("spot_id");
                LocalDateTime entryTime = LocalDateTime.parse(rs.getString("entry_time"), DB_FORMATTER);

                Vehicle vehicle = new Vehicle(plateNumber, Vehicle.VehicleType.CAR, true); // Assuming CAR type for simplicity
                Ticket ticket = new Ticket(ticketID, vehicle, spotID, entryTime);
                ticket.ticketID = ticketID;
                ticket.entryTime = entryTime;

                tickets.add(ticket);
            }
            
        } catch (Exception e) {
        }
        return tickets;
    }


    


   

   
}