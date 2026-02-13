package Model;


import Controller.Fines;
import Controller.ParkingFine;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


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
    String entryTimeStr = null;
    double hourlyRate = 0.0;
    String ticketID = null;

    // PHASE 1: Data Collection
    // We open the connection, get what we need, and close it IMMEDIATELY.
    try (Connection conn = new Data.Sqlite().connect()) {
        String sql = "SELECT t.entry_time, s.hourly_rate, t.ticket_number FROM Tickets t " +
                     "JOIN Parking_Spots s ON t.spot_id = s.spot_id " +
                     "WHERE t.license_plate = ? AND t.exit_time IS NULL";

        try (java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    entryTimeStr = rs.getString("entry_time");
                    hourlyRate = rs.getDouble("hourly_rate");
                    ticketID = rs.getString("ticket_number");
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "Error reading ticket: " + e.getMessage();
    }

    // PHASE 2: Logic & Calculations
    // If no ticket was found, we exit early before touching the Fines logic.
    if (ticketID == null) {
        return "No active ticket found for plate: " + plate;
    }

    // Perform calculations (No database connection is open here)
    this.lastHours = calculateHour(entryTimeStr);
    this.baseFee = calculateBaseFee(hourlyRate, this.lastHours);
    
    String schemeStr = ParkingFine.getInstance().getActiveFineScheme();
    int fineOption = mapSchemeToOption(schemeStr);
    
    // Calculate the fine for THIS specific session
    double currentSessionFine = calculateFine(fineOption, this.baseFee, (int)this.lastHours);

    // Get historical unpaid fines from the ledger
    Fines fineController = new Fines();
    double historicalFines = fineController.getUnpaidLedgerTotal(plate);
    
    // Total fine displayed to user = Current Stay Fine + Old Unpaid Fines
    this.fineAmount = historicalFines + currentSessionFine;

    // PHASE 3: Record Fine
    // Only record to the ledger if the current session actually generated a fine.
    if (currentSessionFine > 0) {
        // This method opens its own fresh connection to WRITE.
        fineController.checkAndRecordFine(plate, "OVERSTAY", currentSessionFine, ticketID);
    }

    // Set start time for receipt generation
    this.startTime = java.time.LocalDateTime.parse(entryTimeStr, formatter)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toEpochSecond();

    return String.format("... Base Fee: RM %.2f, \n Current Fine: RM %.2f, \n Past Unpaid: RM %.2f ...", 
                         this.baseFee, currentSessionFine, historicalFines);
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


public double getRevenue(String dateFrom , String dateTo){
    double totalRevenue = 0.0;
    try (Connection conn = new Data.Sqlite().connect()) {

         String sql = "SELECT SUM(parking_fee) AS total_revenue " +
         "FROM Tickets " +
         "WHERE payment_status = 'PAID' AND exit_time BETWEEN ? AND ?";

        var pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, dateFrom + " 00:00:00");
        pstmt.setString(2, dateTo + " 23:59:59");
        var rs = pstmt.executeQuery();
        
        if (rs.next()){
          totalRevenue = rs.getDouble("total_revenue");
        }
          
        } 
    catch (Exception e) {
        e.printStackTrace();
}
    return totalRevenue;
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



    public List<Object[]> getPaidTicket(){
        List<Object[]> paidTickets = new java.util.ArrayList<>();
        try (Connection conn = new Data.Sqlite().connect()) {

             String sql = "SELECT license_plate, entry_time, exit_time, parking_fee, payment_method " +
             "FROM Tickets " +
             "WHERE payment_status = 'PAID'";

            var pstmt = conn.prepareStatement(sql);
            var rs = pstmt.executeQuery();
            
            while (rs.next()){
              Object[] row = new Object[5];
              row[0] = rs.getString("license_plate");
              row[1] = rs.getString("entry_time");
              row[2] = rs.getString("exit_time");
              row[3] = rs.getDouble("parking_fee");
              row[4] = rs.getString("payment_method");

              for (int i = 0; i < row.length; i++) {
                  if (row[i] == null) {
                      row[i] = "N/A";
                  }
              }
              paidTickets.add(row);
            }
              
            } 
        catch (Exception e) {
            e.printStackTrace();
    }
    return paidTickets;
}


  


   private int mapSchemeToOption(String scheme) {
    if (scheme == null) return -1;
    String upperScheme = scheme.toUpperCase(); // Convert to uppercase to ignore case
    if (upperScheme.contains("OPTION A")) return OPTION_FIXED;
    if (upperScheme.contains("OPTION B")) return OPTION_PROGRESSIVE;
    if (upperScheme.contains("OPTION C")) return OPTION_HOURLY;
    return -1;
}


public boolean processFinalPayment(String plate, double amountPaid, long hours , String paymentMethod){ {
    try (Connection conn = new Data.Sqlite().connect()) {
       conn.setAutoCommit(false); // Enable transaction for data safety
       String sql = "UPDATE Tickets SET payment_status = 'PAID', exit_time = datetime('now'), " +
                 "parking_fee = ?, duration_hours = ? , payment_method = ? " +
                 "WHERE license_plate = ? AND payment_status = 'UNPAID'";


        var pstmt = conn.prepareStatement(sql);


        pstmt.setDouble(1, this.baseFee); // matches first ?
        pstmt.setLong(2, hours);       // matches second ?
        pstmt.setString(3, paymentMethod);
        pstmt.setString(4, plate);

        int rowsUpdated = pstmt.executeUpdate();
        
        if (rowsUpdated > 0){

            double balanceForFines = amountPaid - this.baseFee;
            if (balanceForFines > 0) {
                String selectFineLedger = "SELECT fine_id , amount AS total_fines FROM Fines_Ledger WHERE license_plate = ? AND status = 'UNPAID' ORDER BY created_at ASC";
                var pstmtFine = conn.prepareStatement(selectFineLedger);
                pstmtFine.setString(1, plate);
                var rs = pstmtFine.executeQuery();

                String fineUpdate = "UPDATE Fines_Ledger SET status = 'PAID' WHERE fine_id = ?";
                var pstmtUpdateFine = conn.prepareStatement(fineUpdate);


                while (rs.next() && balanceForFines > 0) {
                    int fineID = rs.getInt("fine_id");
                    double fineAmount = rs.getDouble("total_fines");

                    if (balanceForFines >= fineAmount) {
                        pstmtUpdateFine.setInt(1, fineID);
                        pstmtUpdateFine.executeUpdate();
                        balanceForFines -= fineAmount;
                    } else {
                        break; 
                    }
                    
                }
            }
            


            String updateSpotSQL = "UPDATE Parking_Spots SET status = 'AVAILABLE', current_vehicle_plate = NULL " +
                                 "WHERE current_vehicle_plate = ?";
            
            var pstmt2 = conn.prepareStatement(updateSpotSQL);
            pstmt2.setString(1, plate);
            pstmt2.executeUpdate();


            conn.commit();

            return  true;

        }
          
        } 
    catch (Exception e) {
        e.printStackTrace();

    }
    return false;
}
}


public String getFinalReceipt(String plate , String paymentMethod , double totalPaid , double fineAmount , double baseFee , long hours , long startTime){ {
    ZoneOffset currentOffset = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());
    LocalDateTime entryDateTime = LocalDateTime.ofEpochSecond(startTime, 0, currentOffset);
    double totalOwed = baseFee + fineAmount;
    double change = totalPaid - totalOwed;

    double balanceDue = (change < 0) ? Math.abs(change) : 0.00;
    double changeGiven = (change > 0) ? change : 0.00;


     
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
           "      THANK YOU! HAVE A SAFE TRIP         \n" +
           "BALANCE DUE   : RM " + String.format("%.2f", balanceDue) + "\n" + 
           "CHANGE GIVEN  : RM " + String.format("%.2f", changeGiven) + "\n" +
           "==========================================";

}









}

public String displayFinalReceipt(String receiptContent) {
    return "********** FINAL RECEIPT **********\n" +
           receiptContent +
           "\n***********************************";
}



}
