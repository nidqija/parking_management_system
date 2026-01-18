package InterfaceLibrary;

import Model.Vehicle.VehicleType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingGroup {
    private List<ParkingSpot> spots;
    private static final String PARKING_DATA_FILE = "Data/parking_spots.csv";

    public ParkingGroup() {
        spots = new ArrayList<>();
        loadSpotsFromCSV();
    }

    private void loadSpotsFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(PARKING_DATA_FILE))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }

                // CSV Format: spot_id, floor_id, spot_type, status
                // Example: F1-R1-S1, 1, Handicapped, Occupied
                String[] data = line.split(",");
                if (data.length < 4) continue;

                String spotID = data[0].trim();
                String typeStr = data[2].trim();  // "Handicapped", "Compact", etc.
                String statusStr = data[3].trim(); // "Occupied", "Available"

                // 1. Map the CSV string to Java Enum
                ParkingSpot.SpotType type = mapStringToSpotType(typeStr);

                // 2. Create the spot
                ParkingSpot spot = new ParkingSpot(spotID, type);

                // 3. Set status
                if (statusStr.equalsIgnoreCase("Occupied")) {
                    spot.occupy();
                }

                spots.add(spot);
            }
            System.out.println("Loaded " + spots.size() + " spots.");

        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private ParkingSpot.SpotType mapStringToSpotType(String csvType) {
        // Normalize the string (remove spaces, ignore case)
        switch (csvType.trim().toLowerCase()) {
            case "compact":
                return ParkingSpot.SpotType.COMPACT;
            case "regular":
                return ParkingSpot.SpotType.REGULAR;
            case "handicapped":
                return ParkingSpot.SpotType.HANDICAPPED;
            case "reserved":
                return ParkingSpot.SpotType.RESERVED;
            default:
                System.out.println("Warning: Unknown spot type '" + csvType + "', defaulting to Regular.");
                return ParkingSpot.SpotType.REGULAR;
        }
    }

    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        return spots.stream()
                .filter(spot -> spot.isAvailableFor(vehicleType))
                .collect(Collectors.toList());
    }

    public ParkingSpot getSpotById(String id) {
        for (ParkingSpot s : spots) {
            if (s.getSpotID().equals(id)) return s;
        }
        return null;
    }
}