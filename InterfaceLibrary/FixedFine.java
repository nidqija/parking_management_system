package InterfaceLibrary;

public class FixedFine implements FineInterface {
    @Override
    public double calculateFine(int userId , int parkingDurationHours , int spotId ) {
        double ratePerHour = 5.0; // Fixed rate per hour
        return parkingDurationHours * ratePerHour;
    }

    @Override
    public String getDescription() {
        return "Fixed Fine: A fixed rate per hour of overstay.";
    }


}
