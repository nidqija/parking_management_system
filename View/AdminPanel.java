
package View;
import Controller.ParkingComplex;
import Model.Admin;
import java.awt.*;
import javax.swing.*;

public class AdminPanel extends JPanel {


    private void styleButton(JButton button, Dimension size) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Centers button in Box
        button.setMaximumSize(size);  // Forces BoxLayout to respect the width
        button.setPreferredSize(size);
        button.setFocusable(false);   // Removes the square focus border around text
    }


    public AdminPanel() {   

// ============================= Frame Setup ===================================== //

        JFrame frame = new JFrame("Admin Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);

//================================================================================== //


// ============================= Main Panel ====================================== //

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel);


        Box verticalMenu = Box.createVerticalBox();
        panel.add(verticalMenu);

        Dimension btnSize = new Dimension(250, 40);


//================================================================================== //



// ============================= title label ===================================== //

        JLabel label = new JLabel("Admin Panel - Manage Parking System");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        ParkingComplex parkingComplex = new ParkingComplex();
        JLabel occupancyRate = new JLabel(String.format("Occupancy Rate: %.2f%%", parkingComplex.complexOccupancyRate()));
        occupancyRate.setFont(new Font("Arial", Font.PLAIN, 16));
        occupancyRate.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel availableparking = new JLabel("Available Parking Spots: " + parkingComplex.getTotalAvailableSpots());
        availableparking.setFont(new Font("Arial", Font.PLAIN, 16));
        availableparking.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel occupiedparking = new JLabel("Occupied Parking Spots: " + parkingComplex.getTotalOccupiedSpots());
        occupiedparking.setFont(new Font("Arial", Font.PLAIN, 16));
        occupiedparking.setAlignmentX(Component.CENTER_ALIGNMENT);


        verticalMenu.add(label);
        verticalMenu.add(availableparking);
        verticalMenu.add(occupiedparking);
        verticalMenu.add(occupancyRate);
        

//================================================================================== //


        verticalMenu.add(Box.createRigidArea(new Dimension(250, 20)));

// ============================= Main Menu =================================== //

       JButton manageSpots = new JButton("Manage Parking Spots");
       styleButton(manageSpots, btnSize);
       verticalMenu.add(manageSpots);
       verticalMenu.add(Box.createRigidArea(new Dimension(250, 20)));

         manageSpots.addActionListener(e -> {
              new ManageFloorPanel(1);
         });

       JButton manageFines = new JButton("Manage Parking Fines");
       styleButton(manageFines, btnSize);
       verticalMenu.add(manageFines);

       manageFines.addActionListener(e -> {
           new ManageFines();
       });
       
       verticalMenu.add(Box.createRigidArea(new Dimension(250, 20)));




       JButton viewOutstandingFines = new JButton("View Outstanding Fines");
       styleButton(viewOutstandingFines, btnSize);
       verticalMenu.add(viewOutstandingFines);

        verticalMenu.add(Box.createRigidArea(new Dimension(250, 20)));


       JButton signOutButton = new JButton("Sign Out");
       styleButton(signOutButton, btnSize);
       verticalMenu.add(signOutButton);

//================================================================================== //

       signOutButton.addActionListener(e -> {
          Admin admin = new Admin("", "");
          admin.signout();
       });







// ============================= Make Frame Visible ============================= //

        frame.setVisible(true);

//================================================================================== //
    
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminPanel());
    }


   
}