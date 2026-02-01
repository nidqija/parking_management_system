package Model;
import java.util.ArrayList;

import Controller.ParkingComplex;
import Data.Sqlite;
import InterfaceLibrary.UserInfo;
import View.AdminPanel;
import View.AdminSignInPanel;

public class Admin implements UserInfo  {

  // attributes for admin username and password //
    private String username;
    private String password;
    private Sqlite sqlite;
    


 // constructor to initialize Admin object //

    public  Admin(String username, String password){
       this.username = username;
       this.password = password;
       this.sqlite = new Sqlite();
    }

// method to validate admin sign-in credentials //

    @Override
    public boolean validateSignin() {
        String sql = "SELECT username, password FROM Admins WHERE username = ? AND password = ?";

        try (var conn = sqlite.connect();
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getUsername());
            pstmt.setString(2, getPassword());
            var rs = pstmt.executeQuery();

            return rs.next();
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

// method to redirect to Admin Panel upon successful sign-in //

    public void executeSignIn() {
        if (validateSignin()) {
            System.out.println("Sign-in successful! Redirecting to Admin Panel...");
            new AdminPanel();
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }


 // method to sign out admin //

    @Override
    public void signout() {
       this.username = null;
       this.password = null;
       new AdminSignInPanel();
    }


    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public ArrayList<Integer> getParkingComplexStatus(){
        // Placeholder for actual occupancy status retrieval logic in floors and parking complex
        ArrayList<Integer> ParkingComplexStatus = new ArrayList<>();
        ParkingComplex parkingComplex = new ParkingComplex();
        int availableSpots = parkingComplex.getTotalAvailableSpots();
        int occupiedSpots = parkingComplex.getTotalOccupiedSpots();
        int totalSpots = availableSpots + occupiedSpots;
        ParkingComplexStatus.add(availableSpots);
        ParkingComplexStatus.add(occupiedSpots);
        ParkingComplexStatus.add(totalSpots);
        return ParkingComplexStatus;
    }




    
}
