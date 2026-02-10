package Model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CalculatorFee {
    

    public static final int OPTION_FIXED = 1;
    public static final int OPTION_PROGRESSIVE = 2;
    public static final int OPTION_HOURLY = 3;
    int option = OPTION_PROGRESSIVE;


    public static double calculateBaseFee(double hourlyRate , long hours){
        return hourlyRate * hours;
    }


    

   public static double calculateFine(int fineOption, double baseFee, int hourParked) {
    if (hourParked <= 24) return 0.0; 

    double fineAmount = 0.0;
    String violationType = "";

   
    switch (fineOption) {
        case OPTION_FIXED: violationType = "FIXED"; break;
        case OPTION_PROGRESSIVE: violationType = "PROGRESSIVE"; break;
        case OPTION_HOURLY: violationType = "HOURLY"; break;
    }

    // 2. Fetch the 'amount' from SQLite
    try (Connection conn = new Data.Sqlite().connect();
         PreparedStatement pstmt = conn.prepareStatement("SELECT amount FROM fine_settings WHERE violation_type = ?")) {
        
        pstmt.setString(1, violationType);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            double dbAmount = rs.getDouble("amount");

            // 3. Apply the specific logic based on the option
            if (fineOption == OPTION_FIXED) {
                fineAmount = dbAmount; 
            } 
            else if (fineOption == OPTION_PROGRESSIVE) {
                int totalDays = (hourParked + 23) / 24; 
                // Uses DB amount (50) as the base, then adds increments (e.g., +100 per day)
                if (totalDays == 2) fineAmount = dbAmount + 100.0;
                else if (totalDays == 3) fineAmount = dbAmount + 100.0 + 150.0;
                else if (totalDays >= 4) fineAmount = dbAmount + 100.0 + 150.0 + 200.0;
                else fineAmount = dbAmount;
            } 
            else if (fineOption == OPTION_HOURLY) {
                // Uses DB amount (20) as the hourly multiplier
                fineAmount = (hourParked - 24) * dbAmount;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return fineAmount;
}

    public String processExit(String plate) {
        try (Connection conn = new Data.Sqlite().connect()) {

             String sql = "SELECT t.entry_time, s.hourly_rate " +
             "FROM Tickets t " +
             "JOIN Parking_Spots s ON t.spot_id = s.spot_id " +
             "WHERE t.license_plate = ? AND t.exit_time IS NULL";

            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, plate);
            var rs = pstmt.executeQuery();
            
            if (rs.next()){
               String entryTimeStr = rs.getString("entry_time");
               double hourlyRate = rs.getDouble("hourly_rate");
               long hours = calculateHour(entryTimeStr);
               
               String schemeStr = Controller.ParkingFine.getInstance().getActiveFineScheme();
               int option = mapSchemeToOption(schemeStr);

               if (option == -1) {
                return "Error: Invalid or no Parking Fine Scheme selected in settings.";
            }
               double baseFee = calculateBaseFee(hourlyRate, hours);
               double fineAmount = calculateFine(option, baseFee, (int)hours);
               return String.format("Base Fee: RM %.2f, Fine: RM %.2f (Total: RM %.2f)", 
                                baseFee, fineAmount, (baseFee + fineAmount));
            }
              
            } 
        catch (Exception e) {
            e.printStackTrace();
            return "Error processing exit for plate: " + plate;
        }
        return "No active ticket found for plate: " + plate;

    };


    private long calculateHour(String entryTimeStr) {
       java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       java.time.LocalDateTime entryTime = java.time.LocalDateTime.parse(entryTimeStr, formatter);  
        java.time.LocalDateTime currentTime = java.time.LocalDateTime.now(java.time.ZoneOffset.UTC);
        java.time.Duration duration = java.time.Duration.between(entryTime, currentTime);
        long minutes = duration.toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);

        System.out.println("Calculated parked hours: " + hours);
        System.out.println("Entry Time: " + entryTimeStr + ", Current Time: " + currentTime.format(formatter));
        
    
        return (hours <= 0) ? 1 : hours; // Ensure at least 1 hour is charged
    }


  


   private int mapSchemeToOption(String scheme) {
    if (scheme == null) return -1;
    String upperScheme = scheme.toUpperCase(); // Convert to uppercase to ignore case
    if (upperScheme.contains("OPTION A")) return OPTION_FIXED;
    if (upperScheme.contains("OPTION B")) return OPTION_PROGRESSIVE;
    if (upperScheme.contains("OPTION C")) return OPTION_HOURLY;
    return -1;
}


public boolean processFinalPayment(String plate, double amountPaid) {
    try (Connection conn = new Data.Sqlite().connect()) {

        String sql = "UPDATE Tickets SET payment_status = 'PAID', exit_time = datetime('now') " +
                     "WHERE license_plate = ? AND payment_status = 'UNPAID'";

        var pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, plate);
        int rowsUpdated = pstmt.executeUpdate();
        
        if (rowsUpdated > 0){
            String updateSpotSQL = "UPDATE Parking_Spots SET status = 'AVAILABLE', current_vehicle_plate = NULL " +
                                 "WHERE current_vehicle_plate = ?";
            
            var pstmt2 = conn.prepareStatement(updateSpotSQL);
            pstmt2.setString(1, plate);
            pstmt2.executeUpdate();
            return  true;
        }
          
        } 
    catch (Exception e) {
        e.printStackTrace();

    }
    return false;
}


public String getFinalReceipt(String plate , String paymentMethod){
     
  return "==========================================\n" +
           "           PARKING OFFICIAL RECEIPT       \n" +
           "==========================================\n" +
           "Plate Number  : " + plate + "\n" +
           "Status        : PAID\n" +
           "Method        : " + paymentMethod + "\n" +
           "------------------------------------------\n" +
           "Entry Time    : [From Database]\n" +
           "Exit Time     : " + java.time.LocalDateTime.now() + "\n" +
           "Duration      : [Hours] Hours\n" +
           "------------------------------------------\n" +
           "Base Fee      : [Hours] x RM [Rate]\n" +
           "Fines Due     : RM [Fine Amount]\n" +
           "------------------------------------------\n" +
           "TOTAL PAID    : RM [Total]\n" +
           "Balance       : RM 0.00\n" +
           "==========================================\n" +
           "      THANK YOU! HAVE A SAFE TRIP         \n";

}








}
