package InterfaceLibrary;

import Model.Vehicle.VehicleType;

public class ParkingSpot {

    // Matches your CSV labels
    public enum SpotType {
        COMPACT,
        REGULAR,
        HANDICAPPED,
        RESERVED
        //electric car etc..
    }

    private String spotID;
    private SpotType type;
    private boolean isOccupied;

    public ParkingSpot(String spotID, SpotType type) {
        this.spotID = spotID;
        this.type = type;
        this.isOccupied = false;
    }

    // Checks if a specific vehicle fits in this specific spot.
    public boolean isAvailableFor(VehicleType vType) {
        if (isOccupied) return false;

        // 1. Reserved spots are off-limits 
        if (this.type == SpotType.RESERVED) {
            return false; 
        }

        // 2. Logic based on Vehicle Type
        switch (vType) {
            case MOTORCYCLE:
                // Rule: Compact spots only
                return this.type == SpotType.COMPACT;

            case CAR:
                // Rule: Compact OR Regular
                return this.type == SpotType.COMPACT || this.type == SpotType.REGULAR;

            case TRUCK:
                // Rule: Regular spots only
                return this.type == SpotType.REGULAR;

            case HANDICAPPED:
                // Rule: Can park in any spot 
                return true; 

            default:
                return false;
        }
    }

    public void occupy() { this.isOccupied = true; }
    public String getSpotID() { return spotID; }
    
    @Override
    public String toString() {
        return spotID + " (" + type + ")";
    }
}