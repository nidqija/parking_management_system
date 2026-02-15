package Controller;

import Data.Sqlite;

public abstract class ParkingSpot {

    protected String spotID;
    protected boolean isOccupied;
    protected String vehicleType; //
    private Sqlite sqlite = new Sqlite();

    public ParkingSpot(String spotID, String vehicleType) {
        this.spotID = spotID;
        this.vehicleType = vehicleType;
        this.isOccupied = false;
    }

    public ParkingSpot() {
    }

    

    public String getSpotID() {
        return spotID;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void occupySpot() {
        isOccupied = true;
    }

    public void vacateSpot() {
        isOccupied = false;
    }




}


