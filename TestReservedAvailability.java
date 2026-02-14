import InterfaceLibrary.ParkingGroup;
import InterfaceLibrary.ParkingSpotInterface;
import Model.Vehicle.VehicleType;
import java.util.List;

/**
 * Test to verify RESERVED spots are now available for general use
 */
public class TestReservedAvailability {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Reserved Spot Availability ===\n");
        
        ParkingGroup parkingGroup = new ParkingGroup();
        
        // Test 1: Check if CAR can see available RESERVED spots
        System.out.println("Test 1: Available spots for CAR type (plate: VIP-222)");
        System.out.println("--------------------------------------------------------");
        List<ParkingSpotInterface> carSpots = parkingGroup.getAvailableSpots(VehicleType.CAR, "VIP-222");
        
        long reservedCount = carSpots.stream()
            .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.RESERVED)
            .count();
        
        long compactCount = carSpots.stream()
            .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.COMPACT)
            .count();
        
        long regularCount = carSpots.stream()
            .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.REGULAR)
            .count();
        
        System.out.println("Total available spots: " + carSpots.size());
        System.out.println("  - RESERVED spots: " + reservedCount + " (should be 5 - the S3 spots)");
        System.out.println("  - COMPACT spots: " + compactCount);
        System.out.println("  - REGULAR spots: " + regularCount);
        
        if (reservedCount > 0) {
            System.out.println("\n✅ SUCCESS: RESERVED spots are now available!");
            System.out.println("\nSample RESERVED spots available:");
            carSpots.stream()
                .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.RESERVED)
                .limit(5)
                .forEach(spot -> System.out.println("  - " + spot.getSpotID() + " (RESERVED - RM 10/hour)"));
        } else {
            System.out.println("\n❌ FAILED: No RESERVED spots available for regular vehicles");
        }
        
        // Test 2: Check if permanently reserved spot still works correctly
        System.out.println("\n\nTest 2: Permanently Reserved Spot (WXY5521)");
        System.out.println("--------------------------------------------------------");
        List<ParkingSpotInterface> vipSpots = parkingGroup.getAvailableSpots(VehicleType.CAR, "WXY5521");
        
        boolean hasF1R1S4 = vipSpots.stream()
            .anyMatch(spot -> spot.getSpotID().equals("F1-R1-S4"));
        
        if (hasF1R1S4) {
            System.out.println("✅ WXY5521 can access their permanently reserved spot F1-R1-S4");
        } else {
            System.out.println("❌ WXY5521 cannot access permanently reserved spot F1-R1-S4");
        }
        
        // Test 3: Check that motorcycles cannot use RESERVED spots
        System.out.println("\n\nTest 3: MOTORCYCLE Restrictions");
        System.out.println("--------------------------------------------------------");
        List<ParkingSpotInterface> bikeSpots = parkingGroup.getAvailableSpots(VehicleType.MOTORCYCLE, "BIKE-123");
        
        long bikeReservedCount = bikeSpots.stream()
            .filter(spot -> spot.getType() == ParkingSpotInterface.SpotType.RESERVED)
            .count();
        
        if (bikeReservedCount == 0) {
            System.out.println("✅ Motorcycles correctly CANNOT use RESERVED spots");
        } else {
            System.out.println("❌ Motorcycles should NOT be able to use RESERVED spots");
        }
    }
}
