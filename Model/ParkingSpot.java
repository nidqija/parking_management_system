package Model;

import Data.Sqlite;
import java.sql.Connection;
import javax.swing.JOptionPane;

public class ParkingSpot {
       private Sqlite sqlite = new Sqlite();

       public void loadParkingInfo( String spotId){

        String sql = "SELECT * FROM Parking_Spots WHERE spot_id = ?";
        
        try (Connection conn = sqlite.connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spotId);
            var rs = pstmt.executeQuery();

            if (rs.next()) {
    // 1. Create the Leaf object
    InterfaceLibrary.ParkingSpotInterface psi = new InterfaceLibrary.ParkingSpotInterface(
        spotId, 
        InterfaceLibrary.ParkingSpotInterface.SpotType.valueOf(rs.getString("spot_type").toUpperCase())
    );

    // 2. Hydrate it
    psi.setDetails(
        rs.getString("reserved_for_plate"),
        rs.getString("current_vehicle_plate"),
        rs.getString("floor_id"),
        rs.getString("status")
    );

    // 3. Use the object's built-in display logic
    JOptionPane.showMessageDialog(null, psi.getDetails(), "Parking Spot Info", JOptionPane.INFORMATION_MESSAGE);
}
        } catch (Exception e) {
            e.printStackTrace();
        }

        }
}
