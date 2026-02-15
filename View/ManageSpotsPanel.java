package View;

import Model.ParkingSpot;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class ManageSpotsPanel extends JPanel {

    public ManageSpotsPanel(MainFrame mainFrame, int floorId) {
        // --- Setup Layout ---
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Header Section ---
        JLabel label = new JLabel("Manage Parking Spots - Floor " + floorId, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = new JButton("Back to Floor Management");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.showManageFloor(floorId));

        this.add(label);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(backButton);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Spots Grid Section ---
        JPanel grid = new JPanel(new GridLayout(0, 5, 10, 10)); // 5 columns
        
        // Example logic: Generate 20 spots per floor
        // In a real app, you might fetch the count from the database
       

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select a spot to manage details"));
        this.add(scrollPane);
    }
}