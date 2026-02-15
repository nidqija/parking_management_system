package Controller;

import Data.Sqlite;
import Model.ParkingSpot;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    public double getFormattedRevenue() {
    Model.CalculatorFee calculator = new Model.CalculatorFee();
    return calculator.getRevenue("2000-01-01", "2099-12-31");
}

    public boolean addParkingSpot(int floorId, String type) {
    // 1. Preset rates based on type
    double rate;
    String upperType = type.toUpperCase();

    switch (upperType) {
        case "REGULAR" -> rate = 5.0;
        case "COMPACT" -> rate = 2.0;
        case "RESERVED" -> rate = 10.0;
        case "HANDICAPPED" -> rate = 1.0;
        default -> rate = 5.0; // Default fallback
    }

    int nextNumber = 1;
        String countSql = "SELECT COUNT(*) FROM Parking_Spots WHERE floor_id = ?";
        try (Connection conn = sqlite.connect();
             var countStmt = conn.prepareStatement(countSql)) {
            countStmt.setInt(1, floorId);
            var rs = countStmt.executeQuery();
            if (rs.next()) {
                nextNumber = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Generate the Custom Spot ID (e.g., F1-R1-S5)
        String generatedId = "F" + floorId +"-R1" + "-S" + nextNumber;

    // 2. Optimized SQL Insert
    String sql = "INSERT INTO Parking_Spots (spot_id, floor_id, spot_type, hourly_rate, status) VALUES (?, ?, ?, ?, 'AVAILABLE')";
    
    try (Connection conn = sqlite.connect();
         var pstmt = conn.prepareStatement(sql)) {
        
       

        pstmt.setString(1, generatedId);
        pstmt.setInt(2, floorId);
        pstmt.setString(3, upperType);
        pstmt.setDouble(4, rate);

        return pstmt.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public boolean deleteSpot(int floorId , String spotId) {
    String sql = "DELETE FROM Parking_Spots WHERE floor_id = ? AND spot_id = ?";
    try (Connection conn = sqlite.connect();
         var pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, floorId);
        pstmt.setString(2, spotId);
        return pstmt.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


public List<String> getSpotsByFloor(int floorId){
    List<String> spots = new ArrayList<>();
    String sql = "SELECT spot_id FROM Parking_Spots WHERE floor_id = ?";
    try (Connection conn = sqlite.connect();
         var pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, floorId);
        var rs = pstmt.executeQuery();
        while (rs.next()) {
            spots.add(rs.getString("spot_id"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return spots;
}


 


     

};
