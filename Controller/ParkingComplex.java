package Controller;

import java.sql.Connection;
import java.sql.ResultSet;

import Data.Sqlite;
import Model.ParkingSpot;

public class ParkingComplex {

    private static Sqlite sqlite = new Sqlite();
    private int totalFloors;
    protected  int totalAvailableSpots;
    protected  int totalOccupiedSpots;

    public ParkingSpot findAvailableSpot() {
        // Logic to find and return an available parking spot
        // for parking spot in parking complex, get available spot
        return null; // Placeholder return
    }

    public void parkVehicle(ParkingSpot spot, String vehiclePlate, String vehicleType) {
        // Logic to park a vehicle in the given spot
        // decrease available spots, increase occupied spots
    }

    public void removeVehicle(ParkingSpot spot) {
        // Logic to remove a vehicle from the given spot
        // increase available spots, decrease occupied spots
    }

    public int getTotalAvailableSpots() {
        String sql = "SELECT COUNT(*) FROM Parking_Spots WHERE status = 'AVAILABLE'";

        try (Connection conn = sqlite.connect()) {
            var pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalAvailableSpots = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total Available Spots: " + totalAvailableSpots);
        return totalAvailableSpots;
    }

    public int getTotalOccupiedSpots() {

        String sql = "SELECT COUNT(*) FROM Parking_Spots WHERE status = 'OCCUPIED'";
        try (Connection conn = sqlite.connect()) {
            var pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalOccupiedSpots = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalOccupiedSpots;
    }

    public float complexOccupancyRate() {
        int totalSpots = getTotalAvailableSpots() + getTotalOccupiedSpots();
        if (totalSpots == 0)
            return 0;
        float value = ((float) getTotalOccupiedSpots() / totalSpots) * 100;
       System.out.println(value);
        return value;
    }

};
