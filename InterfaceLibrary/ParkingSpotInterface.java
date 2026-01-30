package InterfaceLibrary;
import Model.Vehicle.VehicleType;

public class ParkingSpotInterface {


    public ParkingSpotInterface(){};

    public enum SpotType {
        COMPACT,
        REGULAR,
        HANDICAPPED,
        RESERVED
    }

    private String spotID;
    private SpotType type;
    private boolean isOccupied;
    private String reservedForPlate;
    private String currentVehiclePlate;
    private String floorId;
    private String status;
    private float hourlyRate;
    


    public ParkingSpotInterface(String spotID, SpotType type) {
        this.spotID = spotID;
        this.type = type;
        this.isOccupied = false;
    }


    public String getReservedForPlate(){
        return reservedForPlate;
    }

    public String getCurrentVehiclePlate(){
        return currentVehiclePlate;
    }

    public String getFloorId(){
        return floorId;
    }

    public String getStatus(){
        return status;
    }

    public float getHourlyRate(){
        return hourlyRate;
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


    public float CalculateRevenue(int hoursParked){
        return hoursParked * hourlyRate;
    }


    public void setDetails(String reservedForPlate, String currentVehiclePlate, String floorId, String status , String hourlyRate){
        this.reservedForPlate = reservedForPlate;
        this.currentVehiclePlate = currentVehiclePlate;
        this.floorId = floorId;
        this.status = status;
        this.hourlyRate = (hourlyRate != null && !hourlyRate.isEmpty()) ? Float.parseFloat(hourlyRate) : 0;
        
    }


    public String getDetails(){
        String details = "Floor ID: " + floorId + "\n" +
                         "Status: " + status + "\n" +
                         "Reserved For Plate: " + (reservedForPlate != null ? reservedForPlate : "N/A") + "\n" +
                         "Current Vehicle Plate: " + (currentVehiclePlate != null ? currentVehiclePlate : "N/A") + "\n" +
                         "Hourly Rate: RM " + hourlyRate ;
        return details;
    }

    
}