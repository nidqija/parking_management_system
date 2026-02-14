package InterfaceLibrary;

import Model.Vehicle.VehicleType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingGroup {
    private List<ParkingSpotInterface> spots;
    private static final String DB_URL = "jdbc:sqlite:Data/Parking_Management_System.db";

    public ParkingGroup() {
        spots = new ArrayList<>();
        loadSpotsFromDB();
    }

    private void loadSpotsFromDB() {
        String query = "SELECT spot_id, spot_type, status, reserved_for_plate FROM Parking_Spots";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String spotID = rs.getString("spot_id");
                String typeStr = rs.getString("spot_type");
                String statusStr = rs.getString("status");
                String reservedPlate = rs.getString("reserved_for_plate");

                // 1. Map DB String to Enum
                ParkingSpotInterface.SpotType type = mapStringToSpotType(typeStr);

                // 2. Create Spot
                ParkingSpotInterface spot = new ParkingSpotInterface(spotID, type);

                // 3. Set reserved plate if present
                if (reservedPlate != null && !reservedPlate.isEmpty()) {
                    spot.setReservedForPlate(reservedPlate);
                }

                // 4. Set Status
                if ("OCCUPIED".equalsIgnoreCase(statusStr)) {
                    spot.occupy();
                }

                spots.add(spot);
            }
            System.out.println("Database: Loaded " + spots.size() + " spots.");

        } catch (SQLException e) {
            System.err.println("Database Error loading spots: " + e.getMessage());
        }
    }

    private ParkingSpotInterface.SpotType mapStringToSpotType(String dbType) {
        if (dbType == null)
            return ParkingSpotInterface.SpotType.REGULAR;

        switch (dbType.trim().toUpperCase()) {
            case "COMPACT":
                return ParkingSpotInterface.SpotType.COMPACT;
            case "REGULAR":
                return ParkingSpotInterface.SpotType.REGULAR;
            case "HANDICAPPED":
                return ParkingSpotInterface.SpotType.HANDICAPPED;
            case "RESERVED":
                return ParkingSpotInterface.SpotType.RESERVED;
            default:
                return ParkingSpotInterface.SpotType.REGULAR;
        }
    }

    public List<ParkingSpotInterface> getAvailableSpots(VehicleType vehicleType) {
        return getAvailableSpots(vehicleType, null);
    }

    public List<ParkingSpotInterface> getAvailableSpots(VehicleType vehicleType, String plateNumber) {
        // Load active time-based reservations from DB
        java.util.Map<String, String> activeReservations = loadActiveReservations();

        // Check 1: Permanent VIP reserved spots (from Parking_Spots.reserved_for_plate)
        if (plateNumber != null && !plateNumber.isEmpty()) {
            List<ParkingSpotInterface> reservedSpots = spots.stream()
                    .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.RESERVED
                            && !"OCCUPIED".equalsIgnoreCase(spot.getStatus())
                            && plateNumber.equalsIgnoreCase(spot.getReservedForPlate()))
                    .collect(Collectors.toList());
            if (!reservedSpots.isEmpty()) {
                return reservedSpots;
            }
        }

        // Check 2: Time-based reservations (from Reservations table)
        if (plateNumber != null && !plateNumber.isEmpty()) {
            // If this plate has an active reservation, return only that spot
            for (java.util.Map.Entry<String, String> entry : activeReservations.entrySet()) {
                if (plateNumber.equalsIgnoreCase(entry.getValue())) {
                    String reservedSpotId = entry.getKey();
                    List<ParkingSpotInterface> timeReserved = spots.stream()
                            .filter(spot -> spot.getSpotID().equals(reservedSpotId)
                                    && spot.isAvailableFor(vehicleType, plateNumber))
                            .collect(Collectors.toList());
                    if (!timeReserved.isEmpty()) {
                        return timeReserved;
                    }
                }
            }
        }

        // Check 3: General pool — exclude spots reserved by others (both permanent and
        // time-based)
        return spots.stream()
                .filter(spot -> spot.isAvailableFor(vehicleType, plateNumber))
                .filter(spot -> {
                    // Exclude spots with active time reservations for OTHER plates
                    String reservedBy = activeReservations.get(spot.getSpotID());
                    if (reservedBy == null)
                        return true; // No reservation, available
                    return reservedBy.equalsIgnoreCase(plateNumber); // Only show if it's YOUR reservation
                })
                .collect(Collectors.toList());
    }

    /**
     * Queries the Reservations table for currently active reservations.
     * Returns a map of spot_id -> license_plate for spots reserved right now.
     */
    private java.util.Map<String, String> loadActiveReservations() {
        java.util.Map<String, String> reservations = new java.util.HashMap<>();
        String query = "SELECT spot_id, license_plate FROM Reservations " +
                "WHERE status = 'ACTIVE' " +
                "AND start_time <= datetime('now', 'localtime') " +
                "AND end_time >= datetime('now', 'localtime')";

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                reservations.put(rs.getString("spot_id"), rs.getString("license_plate"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading reservations: " + e.getMessage());
        }
        return reservations;
    }

    public ParkingSpotInterface getSpotById(String id) {
        for (ParkingSpotInterface s : spots) {
            if (s.getSpotID().equals(id))
                return s;
        }
        return null;
    }

    // Helper to refresh data (e.g. after a new car enters)
    public void refresh() {
        spots.clear();
        loadSpotsFromDB();
    }

    public List<ParkingSpotInterface> getAllSpots() {
        return this.spots; // returs all parking spots loaded from db
    }

}