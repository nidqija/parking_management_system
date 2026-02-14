package View;

import Controller.Floors;
import java.awt.*;
import javax.swing.*;

public class ManageFloorPanel extends JPanel {

    private int floorId;
    private int totalFloor = 5;

    public ManageFloorPanel(MainFrame mainFrame, int floorId) {

        
        // ============================= Frame Setup
        // ===================================== //
        this.floorId = floorId;

        // ==================================================================================
        // //

        // ============================= Main Panel
        // ====================================== //
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Floors Floors = new Floors(floorId);
        JLabel label = new JLabel("Manage Floor: " + floorId, SwingConstants.CENTER);
        JLabel occupancyRate = new JLabel(String.format("Occupancy Rate: %.2f%%", Floors.floorOccupancyRate(floorId)));
        JLabel availableParking = new JLabel("Available Parking Spots: " + Floors.getAvailableSpots(floorId) );
        JLabel occupiedParking = new JLabel("Occupied Parking Spots: " + Floors.getOccupiedSpots(floorId) );
        
        

        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        availableParking.setAlignmentX(Component.CENTER_ALIGNMENT);
        occupiedParking.setAlignmentX(Component.CENTER_ALIGNMENT);
        occupancyRate.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(availableParking);
        panel.add(occupiedParking);
        panel.add(occupancyRate);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ==================================================================================
        // //

        // ============================= Floor Selection Buttons
        // ======================== //

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        for (int i = 0; i < totalFloor; i++) {
            int selectedFloor = i + 1;
            JButton floorButton = new JButton("Floor " + (i + 1));

            if (selectedFloor == floorId) {
                floorButton.setBackground(Color.LIGHT_GRAY);
                floorButton.setEnabled(false);
            }

            floorButton.addActionListener(e -> {
               mainFrame.showManageFloor(selectedFloor);
            });

            // add button to the row
            buttonRow.add(floorButton);
        }

        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> {
            mainFrame.showPage("AdminPanel");
        });
        panel.add(buttonRow);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // ============================= Parking Spots Grid
        // ============================= //

        JPanel grid = new JPanel(new GridLayout(0, 5, 10, 10)); // 4 columns, dynamic rows
        InterfaceLibrary.SpotUIFactory factory = new InterfaceLibrary.SpotUIFactory();
        factory.loadParkingSpots(grid, floorId); // load spots for the specific floor from parking spot class

        // ============================= Parking Spots Grid Wrapper
        // ===================== //

        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.add(grid);

        // Add scroll pane for the grid wrapper
        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Floor Layout"));
        panel.add(scrollPane);

        add(panel);

        setVisible(true);
    }

    
}
