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

    // 1. Database FORMAT, so that easy to query later, this is what will be stored in DB
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 2. REQUIREMENT FORMAT, this is what will be displayed on ticket
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    public Ticket(Vehicle vehicle, String spotID) {
        this.vehicle = vehicle;
        this.spotID = spotID;
        this.entryTime = LocalDateTime.now().withNano(0);
        this.ticketID = generateTicketID();

        saveToDB();
    }

    public Ticket(String ticketID, Vehicle vehicle, String spotID, LocalDateTime entryTime) {
    this.ticketID = ticketID;
    this.vehicle = vehicle;
    this.spotID = spotID;
    this.entryTime = entryTime;
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
        String checkReservationSQL = "SELECT reservation_id FROM Reservations " +
                                      "WHERE license_plate = ? AND spot_id = ? AND status = 'ACTIVE' " +
                                      "LIMIT 1";
        String insertTicketSQL = "INSERT INTO Tickets(ticket_number, license_plate, spot_id, entry_time, payment_status, reservation_id) VALUES(?,?,?,?,?,?)";
        String updateSpotSQL = "UPDATE Parking_Spots SET status = 'OCCUPIED', current_vehicle_plate = ? WHERE spot_id = ?";
        String updateReservationSQL = "UPDATE Reservations SET status = 'COMPLETED' WHERE reservation_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false); // Transaction

            // 0. Check for active reservation
            Integer reservationId = null;
            try (PreparedStatement pstmt = conn.prepareStatement(checkReservationSQL)) {
                pstmt.setString(1, this.vehicle.getPlateNumber());
                pstmt.setString(2, this.spotID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        reservationId = rs.getInt("reservation_id");
                        System.out.println("Found active reservation ID: " + reservationId);
                    }
                }
            }

            // 1. Insert Ticket with reservation_id if found
            try (PreparedStatement pstmt = conn.prepareStatement(insertTicketSQL)) {
                pstmt.setString(1, this.ticketID);
                pstmt.setString(2, this.vehicle.getPlateNumber());
                pstmt.setString(3, this.spotID);
                pstmt.setString(4, this.entryTime.format(DB_FORMATTER)); 
                pstmt.setString(5, "UNPAID");
                if (reservationId != null) {
                    pstmt.setInt(6, reservationId);
                } else {
                    pstmt.setNull(6, java.sql.Types.INTEGER);
                }
                pstmt.executeUpdate();
            }

            // 2. Update Spot Status
            try (PreparedStatement pstmt = conn.prepareStatement(updateSpotSQL)) {
                pstmt.setString(1, this.vehicle.getPlateNumber());
                pstmt.setString(2, this.spotID);
                pstmt.executeUpdate();
            }

            // 3. Mark reservation as completed if it exists
            if (reservationId != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(updateReservationSQL)) {
                    pstmt.setInt(1, reservationId);
                    pstmt.executeUpdate();
                    System.out.println("Marked reservation " + reservationId + " as COMPLETED");
                }
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