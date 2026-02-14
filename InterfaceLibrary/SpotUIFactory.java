package InterfaceLibrary;

import Data.Sqlite;
import Model.ParkingSpot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import javax.swing.JButton;
import javax.swing.JPanel;

public class SpotUIFactory {

    private Sqlite sqlite = new Sqlite();


     // Method to load parking spots into a given JPanel grid for a specific floor //

       public void loadParkingSpots(JPanel grid, int floorId) {
    // JOIN allows us to see if there is an ACTIVE reservation for the current time
    String sql = "SELECT p.spot_id, p.spot_type, p.status, r.license_plate AS reserved_plate " +
                 "FROM Parking_Spots p " +
                 "LEFT JOIN Reservations r ON p.spot_id = r.spot_id " +
                 "AND r.status = 'ACTIVE' " +
                 "AND datetime('now', 'localtime') BETWEEN r.start_time AND r.end_time " +
                 "WHERE p.floor_id = ?";

    try (Connection conn = sqlite.connect()) {
        var pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, floorId);
        var rs = pstmt.executeQuery();

        while (rs.next()) {
            String spotID = rs.getString("spot_id");
            String type = rs.getString("spot_type");
            String status = rs.getString("status");
            String reservedPlate = rs.getString("reserved_plate");

            // Decide text based on status
            String btnText = "<html><center>" + spotID + "<br>" + type;
            if (reservedPlate != null && !"OCCUPIED".equals(status)) {
                btnText += "<br><font color='blue'>RESERVED: " + reservedPlate + "</font>";
            } else {
                btnText += "<br>(" + status + ")";
            }
            btnText += "</center></html>";

            JButton spotButton = new JButton(btnText);
            
            // Pass the reservation status to the styling method
            styleButton(spotButton, status, reservedPlate != null);
            
            grid.add(spotButton);

            spotButton.addActionListener(e -> {
                ParkingSpot ps = new ParkingSpot();
                ps.loadParkingInfo(spotID);
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

 // Method to style parking spot buttons based on their status //
 
  private void styleButton(JButton button, String status, boolean isReserved) {
        button.setFocusable(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 60));
        button.setFont(new Font("Arial" , Font.PLAIN , 12));

        if("OCCUPIED".equals(status)) {
            button.setBackground(new Color(255, 102, 102)); // Light Red
        } else if (isReserved) {
            button.setBackground(new Color(255, 255, 102)); // Light Yellow
        } else {
            button.setBackground(new Color(144, 238, 144)); // Light Green
        }
    }

}
