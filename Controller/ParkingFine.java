package Controller;

import Data.Sqlite;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class ParkingFine {
    
    private static ParkingFine instance;
    private Map<String, Double> fineRates = new HashMap<>();
    private String activeScheme = "Option A";


    private ParkingFine(){

    }

    public void refreshFineRates(){
        try (Connection conn = new Sqlite().connect()){
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT violation_type, amount FROM Fine_Settings");

            fineRates.clear();
            while (rs.next()) {
                fineRates.put(rs.getString("violation_type"), rs.getDouble("amount"));
            }

            System.out.println("Fine rates refreshed: " + fineRates);

        } catch (Exception e) {
            e.printStackTrace();
    }

   
    }


    public void setActiveScheme(String scheme){
        this.activeScheme = scheme;
         
        String sql = "UPDATE Active_Scheme_Settings SET active_scheme = ? WHERE id = 1";

        try (Connection conn = new Sqlite().connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, scheme);
            pstmt.executeUpdate();
            System.out.println("Active fine scheme set to: " + scheme);

            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadActiveScheme(){
        String sql = "SELECT active_scheme FROM Active_Scheme_Settings WHERE id = 1";

        try (Connection conn = new Sqlite().connect()){
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            if (rs.next()) {
                this.activeScheme = rs.getString("active_scheme");
                System.out.println("Loaded active fine scheme: " + this.activeScheme);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getActiveScheme(){
        return activeScheme;
    }


    public Map<String, Double> getFineRatesMap() {
        return fineRates;
    }


     
  


    public static ParkingFine getInstance() {
        if (instance == null) {
            instance = new ParkingFine();
        }
        return instance;

    }


     public double getFineAmount(String violationType) {
        return fineRates.getOrDefault(violationType, 0.0);
    }


    public void updateRate(String violationType , double newAmount){
        String sql = "UPDATE Fine_Settings SET amount = ? WHERE violation_type = ?";

        try (Connection conn = new Sqlite().connect()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2,violationType);
            pstmt.executeUpdate();
            refreshFineRates();

            // Update in-memory map
            fineRates.put(violationType, newAmount);
            System.out.println("Updated fine rate for " +violationType + " to " + newAmount);
            
        } catch (Exception e) {
        }
    }

}
