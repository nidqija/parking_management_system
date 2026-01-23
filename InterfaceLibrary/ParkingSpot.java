package InterfaceLibrary;
import Model.Vehicle.VehicleType;

public class ParkingSpot {


    public ParkingSpot(){};

    public enum SpotType {
        COMPACT,
        REGULAR,
        HANDICAPPED,
        RESERVED
    }

    private String spotID;
    private SpotType type;
    private boolean isOccupied;

    public ParkingSpot(String spotID, SpotType type) {
        this.spotID = spotID;
        this.type = type;
        this.isOccupied = false;
    }

    public boolean isAvailableFor(VehicleType vType) {
        if (isOccupied) return false;

        // 1. Reserved spots are off-limits for general entry
        if (this.type == SpotType.RESERVED) {
            return false; 
        }

        // 2. Logic based on Vehicle Type
        switch (vType) {
            case MOTORCYCLE:
                return this.type == SpotType.COMPACT; // Compact only
            case CAR:
                return this.type == SpotType.COMPACT || this.type == SpotType.REGULAR;
            case TRUCK: // or SUV
                return this.type == SpotType.REGULAR; // Regular only
            case HANDICAPPED:
                return true; // Any spot
            default:
                return false;
        }
    }

    public void occupy() { this.isOccupied = true; }
    
    public String vacate() { 
        this.isOccupied = false; 
        return this.spotID; 
    }

    public String getSpotID() { return spotID; }
    public SpotType getType() { return type; } // Added getter

    @Override
    public String toString() {
        return spotID + " (" + type + ")";
    }


   
  

    


    






    
}