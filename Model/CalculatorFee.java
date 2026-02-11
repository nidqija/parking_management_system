package Model;


import Controller.ParkingFine;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class CalculatorFee {
    

    public static final int OPTION_FIXED = 1;
    public static final int OPTION_PROGRESSIVE = 2;
    public static final int OPTION_HOURLY = 3;
    int option = OPTION_PROGRESSIVE;
    private double baseFee;
    private double fineAmount;
    private long lastHours;
    private long startTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");




    public static double calculateBaseFee(double hourlyRate , long hours){
        return hourlyRate * hours;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public long getLastHours() {
        return lastHours;
    }

    public double getTotalAmount(){
        return baseFee + fineAmount;
    }

    public long getStartTime(){
        return startTime;
    }

    


    

   public static double calculateFine(int fineOption, double baseFee, int hourParked) {
  

    if (hourParked <= 24) return 0.0;

    double fineAmount = 0.0;
    ParkingFine fineController = ParkingFine.getInstance();

    double dbAmount = 0.0;
    switch (fineOption) {
        case OPTION_FIXED:
            dbAmount = fineController.getFineAmount("FIXED");
            fineAmount = dbAmount;
            break;
        case OPTION_PROGRESSIVE:
            dbAmount = fineController.getFineAmount("PROGRESSIVE");
            int totalDays = (hourParked + 23) / 24;

            if (totalDays <= 1) {
            fineAmount = dbAmount; // RM 50 for first day
          } else if (totalDays == 2) {
               fineAmount = dbAmount + 100.0; // Total: 150
            } else if (totalDays == 3) {
                fineAmount = dbAmount + 100.0 + 150.0; // Total: 300
            } else if (totalDays >= 4) {
                fineAmount = dbAmount + 100.0 + 150.0 + 200.0 ; 
            }
            break;
        case OPTION_HOURLY:
            dbAmount = fineController.getFineAmount("HOURLY");
            int hoursOverstay = hourParked - 24;
            fineAmount = dbAmount * hoursOverstay;
            break;
        default:
            fineAmount = 0.0; 
    }


    return fineAmount;
}

public String processExit(String plate) {
    try (Connection conn = new Data.Sqlite().connect()) {
        String sql = "SELECT t.entry_time, s.hourly_rate FROM Tickets t " +
                     "JOIN Parking_Spots s ON t.spot_id = s.spot_id " +
                     "WHERE t.license_plate = ? AND t.exit_time IS NULL";

        var pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, plate);
        var rs = pstmt.executeQuery();

        if (rs.next()) {
            String entryTimeStr = rs.getString("entry_time");
            double hourlyRate = rs.getDouble("hourly_rate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            // USE SYSTEM LOCAL TIME
            java.time.LocalDateTime currentTime = java.time.LocalDateTime.now();

            this.lastHours = calculateHour(entryTimeStr);
            this.baseFee = calculateBaseFee(hourlyRate, this.lastHours);
            
            String schemeStr = ParkingFine.getInstance().getActiveFineScheme();
            int fineOption = mapSchemeToOption(schemeStr);
            this.fineAmount = calculateFine(fineOption, this.baseFee, (int)this.lastHours);

            // Convert to EpochSecond using the system default offset
            this.startTime = java.time.LocalDateTime.parse(entryTimeStr, formatter)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toEpochSecond();

             return String.format("\n\n====================Payment Details====================\n Base Fee: RM %.2f,\n Fine: RM %.2f \n Start Time: %s\n End Time: %s\n Parked for: %d hours parked.\n======================================================\n", 
                                     this.baseFee, this.fineAmount, entryTimeStr, currentTime.format(formatter), this.lastHours);

        }
    } catch (Exception e) {
        e.printStackTrace();
        return "Error: " + e.getMessage();
    }
    return "No active ticket found for plate: " + plate;
}

 
    public double getPreviousFines(String plate){
        double totalPreviousFines = 0.0;
        try (Connection conn = new Data.Sqlite().connect()) {

             String sql = "SELECT SUM(parking_fee) AS total_fines " +
             "FROM Tickets " +
             "WHERE license_plate = ? AND payment_status = 'PAID'";

            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, plate);
            var rs = pstmt.executeQuery();
            
            if (rs.next()){
              totalPreviousFines = rs.getDouble("total_fines");
            }
              
            } 
        catch (Exception e) {
            e.printStackTrace();
    }
    return totalPreviousFines;
}


    private long calculateHour(String entryTimeStr) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        java.time.LocalDateTime entryTime = java.time.LocalDateTime.parse(entryTimeStr, formatter);  
        java.time.LocalDateTime currentTime = java.time.LocalDateTime.now();
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


public boolean processFinalPayment(String plate, double amountPaid, long hours) {
    try (Connection conn = new Data.Sqlite().connect()) {

       String sql = "UPDATE Tickets SET payment_status = 'PAID', exit_time = datetime('now'), " +
                 "parking_fee = ?, duration_hours = ? " +
                 "WHERE license_plate = ? AND payment_status = 'UNPAID'";


        var pstmt = conn.prepareStatement(sql);


        pstmt.setDouble(1, amountPaid); // matches first ?
        pstmt.setLong(2, hours);       // matches second ?
        pstmt.setString(3, plate);
        int rowsUpdated = pstmt.executeUpdate();
        
        if (rowsUpdated > 0){

            String updatedFineLedger = "UPDATE Fines_Ledger SET status = 'PAID' WHERE license_plate = ? AND status = 'UNPAID'";
            var pstmt1 = conn.prepareStatement(updatedFineLedger);
            pstmt1.setString(1, plate);
            pstmt1.executeUpdate();


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


public String getFinalReceipt(String plate , String paymentMethod , double totalPaid , double fineAmount , double baseFee , long hours , long startTime){ {
    ZoneOffset currentOffset = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());
    LocalDateTime entryDateTime = LocalDateTime.ofEpochSecond(startTime, 0, currentOffset);
     
  return "==========================================\n" +
           "           PARKING OFFICIAL RECEIPT       \n" +
           "==========================================\n" +
           "Plate Number  : " + plate + "\n" +
           "Status        : PAID\n" +
           "Method        : " + paymentMethod + "\n" +
           "------------------------------------------\n" +
           "Entry Time    : " + entryDateTime.format(formatter) + "\n" +
           "Exit Time     : " + LocalDateTime.now().format(formatter) + "\n" +
           "Duration      : " + hours + " Hours\n" +
           "------------------------------------------\n" +
           "Base Fee      : RM " + baseFee + "\n" +
           "Fines Due     : RM " + fineAmount + "\n" +
           "------------------------------------------\n" +
           "TOTAL PAID    : RM " + totalPaid + "\n" +
           "Balance       : RM 0.00\n" +
           "==========================================\n" +
           "      THANK YOU! HAVE A SAFE TRIP         \n";

}










}
}
