package InterfaceLibrary;

import java.sql.Connection;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import Data.Sqlite;

public class SpotUIFactory {

    private Sqlite sqlite = new Sqlite();
     // Method to load parking spots into a given JPanel grid for a specific floor //

       public void loadParkingSpots(JPanel grid , int floorId){
        String sql = "SELECT spot_id, spot_type, status FROM Parking_Spots WHERE floor_id = ?";
        
        try (Connection conn = sqlite.connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, floorId);
            var rs = pstmt.executeQuery();

            while (rs.next()) {
                String spotID = rs.getString("spot_id");
                String type = rs.getString("spot_type");
                String status = rs.getString("status");

                JButton spotButton = new JButton(spotID + " - " + type + " (" + status + ")");
                styleButton(spotButton, status);
                grid.add(spotButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
    }
}

 // Method to style parking spot buttons based on their status //
 
  private void styleButton(JButton button, String status) {
        button.setFocusable(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(100, 60));
        button.setFont(new Font("Arial" , Font.PLAIN , 12));

        if("OCCUPIED".equals(status)) {
            button.setBackground(new Color(255, 102, 102)); // Light Red
        } else {
            button.setBackground(new Color(144, 238, 144)); // Light Green
        }
    }

}
