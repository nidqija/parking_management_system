package Model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import Data.Sqlite;

public class Reservation {


    public void completeReservation(String plate, String spot_id) {
        String sql = "UPDATE Reservations SET status = 'completed' WHERE spot_id = ? AND license_plate = ?";
        Sqlite db = new Sqlite();
        try (Connection conn = db.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, spot_id);
            pstmt.setString(2, plate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
