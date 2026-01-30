package InterfaceLibrary;

public class ProgressiveFine implements FineInterface {
    @Override
    public double calculateFine(int userId , int parkingDurationHours , int spotId ) {
        double baseRate = 3.0; // Base rate for the first hour
        double additionalRate = 4.0; // Rate for each additional hour
        if (parkingDurationHours <= 1) {
            return baseRate;
        } else {
            return baseRate + (parkingDurationHours - 1) * additionalRate;
        }
    }

    @Override
    public String getDescription() {
        return "Progressive Fine: A base rate for the first hour and a higher rate for each additional hour.";
    }
    
}
