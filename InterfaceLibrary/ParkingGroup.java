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
        // If plate has a reserved spot, return only that spot
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
        // Otherwise return normal available spots
        return spots.stream()
                .filter(spot -> spot.isAvailableFor(vehicleType, plateNumber))
                .collect(Collectors.toList());
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