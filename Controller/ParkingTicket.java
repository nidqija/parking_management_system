package Controller;
import Model.Ticket;
import java.util.List;


public class ParkingTicket {
    private static ParkingTicket instance = null;
    private Ticket currentTicket;

    private ParkingTicket() {
        // Private constructor to prevent instantiation
    }

    public static ParkingTicket getInstance() {
        if (instance == null) {
            instance = new ParkingTicket();
        }
        return instance;
    }

    public void createNewTicket(Model.Vehicle vehicle, String spotID) {
        currentTicket = new Ticket(vehicle, spotID);
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }


    public Object[][] getTableDataforTicket() {

        List<Ticket> tickets = Ticket.getAllTickets();
        Object[][] data = new Object[tickets.size()][5];
        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            data[i][0] = ticket.getTicketID();
            data[i][1] = ticket.getPlateNumber();
            data[i][2] = ticket.getSpotID();
            data[i][3] = ticket.getEntryTimeStr();
            data[i][4] = "UNPAID"; 
        }

        return data;
    }
}