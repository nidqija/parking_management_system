package Controller;
import Model.ParkingSpot;

import java.sql.Connection;
import java.sql.ResultSet;
import Data.Sqlite;
import Model.ParkingSpot;

public class Floors {

    private static Sqlite sqlite = new Sqlite();
    private int floorNumber;
    protected  int Rows;
    protected int totalSpots;
    protected  int availableSpots;
    protected  int occupiedSpots;

     public Floors(int floorNumber) {
        this.floorNumber = floorNumber;
        this.totalSpots = availableSpots + occupiedSpots;
    }

    

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

    public int getTotalSpots() {
        
        totalSpots = availableSpots + occupiedSpots;
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

    
}
