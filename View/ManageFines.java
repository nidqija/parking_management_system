package View;
import Controller.ParkingFine;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ManageFines extends JPanel {
    public ManageFines() {
        //setTitle("Manage Fines");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        //setLocationRelativeTo(null);
        setVisible(true);


        JLabel label = new JLabel("Manage Fines Panel");
        add(label);


        ParkingFine parkingFine = ParkingFine.getInstance();
        parkingFine.refreshFineRates();

        add(new JLabel("Overstay Night RM50"));
        JTextArea overstayArea = new JTextArea("Current Rate: RM" + parkingFine.getFineAmount("Illegal_Parking"));
        add(overstayArea);

       

      



    }


    
}
