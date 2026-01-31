import Model.CalculatorFee;
import Model.CalculatorFee.ActiveTicket;
import java.time.LocalDateTime;

public class TestCalculatorFee {
    public static void main(String[] args) {
        String plate = "APT999";
        ActiveTicket t = CalculatorFee.findActiveTicket(plate);
        if (t == null) {
            System.out.println("No active ticket for " + plate);
            return;
        }
        System.out.println("Found ticket: " + t.ticketID + " spot=" + t.spotID + " entry=" + t.entryTime);
        LocalDateTime exit = LocalDateTime.now();
        long hours = CalculatorFee.calculateHours(t.entryTime, exit);
        double fee = CalculatorFee.calculateFee(t.entryTime, t.spotID, exit);
        System.out.println("Hours: " + hours + " Fee: RM " + String.format("%.2f", fee));
    }
}