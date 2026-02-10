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

public class Vehicle {

    public enum VehicleType {
        CAR, MOTORCYCLE, TRUCK, HANDICAPPED  //add more types as needed 
    }
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String plateNumber;
    private VehicleType type;
    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";
    private String ticketNumber;
    private String spotID;
    private String spotType;
    private String spotStatus;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String paymentStatus;


    public Vehicle(String plateNumber, VehicleType type) {
        this.plateNumber = plateNumber;
        this.type = type;

        registerInDB();
    }


    public Vehicle(String plateNumber, VehicleType type, boolean isExisting) {
    this.plateNumber = plateNumber;
    this.type = type;
   
   }

   //overloaded constructor just for report :/
   public Vehicle(String plateNumber, VehicleType type, String ticketNumber, String spotID, 
                    LocalDateTime entryTime, LocalDateTime exitTime, String paymentStatus) {
        this.plateNumber = plateNumber;
        this.type = type;
        this.ticketNumber = ticketNumber;
        this.spotID = spotID;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.paymentStatus = paymentStatus;
    }

   
    //getters
    public String getPlateNumber() { return plateNumber; }
    public VehicleType getType() { return type; }
    public String getTicketNumber() { return ticketNumber; }
    public String getSpotID() { return spotID; }
    public String getSpotType() { return spotType; }
    public String getSpotStatus() { return spotStatus; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public String getPaymentStatus() { return paymentStatus; }

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





    public static List<Vehicle> getVehicleReportList() { //v for vehicle, t for ticket, ps for parking spot
        List<Vehicle> vehicles = new ArrayList<>();
        //ai generated query
        String query = 
        """
        SELECT v.license_plate, v.vehicle_type, t.ticket_number, t.entry_time, t.exit_time,
               t.payment_status, t.spot_id, ps.spot_type, ps.status AS spot_status, ps.floor_id
        FROM Vehicles v
        LEFT JOIN Tickets t ON v.license_plate = t.license_plate
        LEFT JOIN Parking_Spots ps ON t.spot_id = ps.spot_id;
        """;


        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                 String plate = rs.getString("license_plate");
                 String typeStr = rs.getString("vehicle_type");
                 Vehicle.VehicleType type;
                switch (typeStr.toUpperCase()) {
                    case "CAR" -> type = Vehicle.VehicleType.CAR;
                    case "SUV" -> type = Vehicle.VehicleType.CAR; // map SUV if needed
                    case "MOTORCYCLE" -> type = Vehicle.VehicleType.MOTORCYCLE;
                    case "TRUCK" -> type = Vehicle.VehicleType.TRUCK;
                    case "HANDICAPPED" -> type = Vehicle.VehicleType.HANDICAPPED;
                    default -> type = Vehicle.VehicleType.CAR;
                }

                String ticketNumber = rs.getString("ticket_number");
                String spotID = rs.getString("spot_id");
                LocalDateTime entryTime = rs.getString("entry_time") != null
                ? LocalDateTime.parse(rs.getString("entry_time"), DB_FORMATTER) : null;
                LocalDateTime exitTime = rs.getString("exit_time") != null
                ? LocalDateTime.parse(rs.getString("exit_time"), DB_FORMATTER) : null;
                String paymentStatus = rs.getString("payment_status");
                
            

                 Vehicle vehicle = new Vehicle(plate, type, ticketNumber, spotID,entryTime, exitTime, paymentStatus);
                 vehicles.add(vehicle);

               
            }
            
        } catch (Exception e) {
        }
        return vehicles;
    }
    
}