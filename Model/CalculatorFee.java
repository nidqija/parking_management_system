package Model;


import java.sql.Connection;


public class CalculatorFee {
    

    public static final int OPTION_FIXED = 1;
    public static final int OPTION_PROGRESSIVE = 2;
    public static final int OPTION_HOURLY = 3;
    int option = OPTION_PROGRESSIVE;


    public static double calculateBaseFee(double hourlyRate , long hours){
        return hourlyRate * hours;
    }


    public static double calculateFine(int fineOption, double baseFee , int hourParked){
        if ( hourParked <= 24) return 0.0;


        switch (fineOption) {
            case OPTION_FIXED:
                return 50.0;
            case OPTION_PROGRESSIVE:
                int totalDays = (hourParked + 23) / 24; // Rounds up (e.g., 25 hours = 2 days)
                if (totalDays <= 1) {
                  return 50.0;
                } else if (totalDays == 2) {
                  return 50.0 + 100.0;
                } else if (totalDays == 3) {
                  return 50.0 + 100.0 + 150.0;
                } else {
                   // Day 4 and beyond
                   return 50.0 + 100.0 + 150.0 + 200.0;
               }
            case OPTION_HOURLY:
                int extraHours = hourParked - 24;
                return extraHours * 20.0;
            default:
                return 0.0;
        }

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
               
               String schemeStr = Controller.ParkingFine.getInstance().getActiveScheme();
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








}
