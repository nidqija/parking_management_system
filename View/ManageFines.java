package View;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ManageFines extends JFrame {
    public ManageFines() {
        setTitle("Manage Fines");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setVisible(true);


        JLabel label = new JLabel("Manage Fines Panel");
        add(label);
    }


    
}
