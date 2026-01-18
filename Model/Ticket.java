package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketID;
    private Vehicle vehicle;
    private String spotID;
    private LocalDateTime entryTime;

    public Ticket(Vehicle vehicle, String spotID) {
        this.vehicle = vehicle;
        this.spotID = spotID;
        this.entryTime = LocalDateTime.now();
        this.ticketID = generateTicketID();
    }

    private String generateTicketID() {
        // Requirement: T-PLATE-TIMESTAMP
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return "T-" + vehicle.getPlateNumber() + "-" + entryTime.format(dtf);
    }

    public String getTicketDetails() {
        return "Ticket ID: " + ticketID + "\n" +
               "Spot: " + spotID + "\n" +
               "Entry: " + entryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters for CSV writing
    public String getTicketID() { return ticketID; }
    public String getSpotID() { return spotID; }
    public String getPlateNumber() { return vehicle.getPlateNumber(); }
    public String getEntryTimeStr() { return entryTime.toString(); }
}