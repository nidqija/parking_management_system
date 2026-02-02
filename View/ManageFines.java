package View;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ManageFines extends JPanel {
    public ManageFines() {
        //setTitle("Manage Fines");
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        //setLocationRelativeTo(null);
        setVisible(true);


        JLabel label = new JLabel("Manage Fines Panel");
        add(label);

        JRadioButton rb1 = new JRadioButton("Option A: Fixed Fine");
        JRadioButton rb2 = new JRadioButton("Option B: Progressive Fine");
        JRadioButton rb3 = new JRadioButton("Option C: Variable Fine");

        add(rb1);
        add(rb2);
        add(rb3);



    }


    
}
