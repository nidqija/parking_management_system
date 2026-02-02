package Controller;

import InterfaceLibrary.FineInterface;

public class ParkingFine {
    
    private static ParkingFine instance;

    private FineInterface fineStrategy;

    private ParkingFine(){
        this.fineStrategy = fineStrategy;
    }

    public static synchronized ParkingFine getInstance(){
        if(instance == null){
            instance = new ParkingFine();
        }
        return instance;
    }


    public void setFineScheme(FineInterface scheme){
        this.fineStrategy = scheme;

    }

    public double calculateFine(int userId, int parkingDurationHours , int spotId ){
        return fineStrategy.calculateFine( userId, parkingDurationHours , spotId );
    }

    


}
