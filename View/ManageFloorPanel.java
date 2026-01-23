package View;
import java.awt.*;
import javax.swing.*;


public class ManageFloorPanel extends JFrame{

    private int floorId;
    private int totalFloor = 3; 

    public ManageFloorPanel( int floorId ) {

    // ============================= Frame Setup ===================================== //
        this.floorId = floorId;
        setTitle("Manage Floor Panel");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    //================================================================================== //


    // ============================= Main Panel ====================================== //
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel , BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Manage Floor: " + floorId, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0 , 20)));

    //================================================================================== //
        
        
    
    // ============================= Floor Selection Buttons ======================== //

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        for ( int i = 0 ; i < totalFloor ; i++){
            int selectedFloor = i + 1;
            JButton floorButton = new JButton("Floor " + (i + 1));
           
            // Highlight current floor button based on floor id 
            if (selectedFloor == floorId) {
                floorButton.setBackground(Color.LIGHT_GRAY); 
                floorButton.setEnabled(false); 
            }

            // floor button action listener , will take to the selected floor panel page
            floorButton.addActionListener(e -> {
                new ManageFloorPanel(selectedFloor);
                dispose();
            });
            
            // add button to the row
            buttonRow.add(floorButton);
        }
        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> {
            new AdminPanel();
            dispose(); 
        });
        panel.add(buttonRow);
        panel.add(Box.createRigidArea(new Dimension(0 , 20)));
        panel.add(backButton);
        panel.add(Box.createRigidArea(new Dimension(0 , 20))); 


        // ============================= Parking Spots Grid ============================= //

        JPanel grid = new JPanel(new GridLayout(0, 5, 10, 10)); // 4 columns, dynamic rows
        InterfaceLibrary.SpotUIFactory factory = new InterfaceLibrary.SpotUIFactory();
        factory.loadParkingSpots(grid, floorId); // load spots for the specific floor from parking spot class


        // ============================= Parking Spots Grid Wrapper ===================== //

        JPanel gridWrapper =  new JPanel(new GridBagLayout());
        gridWrapper.add(grid);

        // Add scroll pane for the grid wrapper
        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Floor Layout"));
        panel.add(scrollPane);
        

        add(panel);
        setVisible(true);
    }





    public static void main(String[] args) {
        new ManageFloorPanel(1);
        
    }
}
