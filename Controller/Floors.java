package Controller;
import Model.ParkingSpot;
import Model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import Data.Sqlite;
import Model.ParkingSpot;

public class Floors {

    private static Sqlite sqlite = new Sqlite();
    private int floorNumber;
    private int numFloors;
    private String floor_name;
    protected  int Rows;
    protected int totalSpots;
    protected  int availableSpots;
    protected  int occupiedSpots;

     public Floors(int floorNumber) {
        this.floorNumber = floorNumber;
        this.totalSpots = availableSpots + occupiedSpots;
    }

     public Floors(int floorNumber, String floor_name) {
        this.floorNumber = floorNumber;
        this.totalSpots = availableSpots + occupiedSpots;
        this.floor_name = floor_name;
    }

    public int getFloorNumber() { return floorNumber; } 
    public int getNumFloors() { return numFloors; } 
    public String getFloorName() { return floor_name; } 
    public int getRows() { return Rows; } 
    public int getAvailableSpots() { return availableSpots; } 
    public int getOccupiedSpots() { return occupiedSpots; }

    

    public ParkingSpot getAvailableSpot(ParkingSpot parkingSpot) {
        // Logic to find and return an available parking spot of the specified type on this floor
        return null; // Placeholder return
    }


    public int getAvailableSpots(int floorid) {
        String sql = "SELECT COUNT(*) FROM Parking_Spots WHERE status = 'AVAILABLE' AND floor_id = ?"; ;

        try (Connection conn = sqlite.connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, floorid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                availableSpots = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return availableSpots;
    }

    public int getOccupiedSpots(int floorid) {
        String sql = "SELECT COUNT(*) FROM Parking_Spots WHERE status = 'OCCUPIED' AND floor_id = ?";

        try (Connection conn = sqlite.connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, floorid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                occupiedSpots = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return occupiedSpots;
    }

    public int getTotalSpots(int floorNumber) {
        
        int totalSpots = getAvailableSpots(floorNumber) + getOccupiedSpots(floorNumber);
        return totalSpots;
    }

    public float floorOccupancyRate(int floorNumber) {
        float totalSpots = getAvailableSpots(floorNumber) + getOccupiedSpots(floorNumber);
        if (totalSpots == 0) {
            return 0;
        }
        float value = (getOccupiedSpots(floorNumber) /  totalSpots) * 100;
        System.out.println(value);
        return value;
    }

    public static List<Floors> getFloors() {
        String sql = "SELECT floor_id, floor_name FROM Floors";
        List<Floors> floors = new ArrayList<>();
        try (Connection conn = sqlite.connect()){
            var pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int floor_number = rs.getInt("floor_id");
                String floor_name = rs.getString("floor_name");


                Floors floor = new Floors(floor_number, floor_name);
                floors.add(floor);
            }

            


        } catch (Exception e) {
            e.printStackTrace();
        }
        return floors;
    }

    
}
