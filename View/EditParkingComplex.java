package View;

import Controller.ParkingComplex;
import java.awt.*;
import javax.swing.*;

public class EditParkingComplex extends JPanel {
    private DefaultListModel<String> listModel;
    private ParkingComplex controller;

    public EditParkingComplex(MainFrame mainFrame, int floorId) {
        this.controller = new ParkingComplex();
        this.listModel = new DefaultListModel<>();
        
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

      
        JLabel label = new JLabel("Edit Floor " + floorId + " Configuration", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        this.add(label, BorderLayout.NORTH);

     
        JList<String> parkingSpotList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(parkingSpotList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Spots on Floor " + floorId));
        this.add(scrollPane, BorderLayout.CENTER); 

      
        Runnable loadSpots = () -> {
            listModel.clear();
            java.util.List<String> spots = controller.getSpotsByFloor(floorId);
            for (String spot : spots) {
                listModel.addElement(spot);
            }
        };

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.showPage("ManageParkingComplex"));
        
        JButton insertBtn = new JButton("Insert New Spot");
        insertBtn.addActionListener(e -> {
           String[] options = {"REGULAR", "COMPACT", "RESERVED", "HANDICAPPED"};

              String vehicleType = (String) JOptionPane.showInputDialog(
                     this,
                     "Select Vehicle Type for New Spot:",
                     "Insert Parking Spot",
                     JOptionPane.PLAIN_MESSAGE,
                     null,
                     options,
                     options[0]
              );

                if (vehicleType != null) {
                    controller.addParkingSpot(floorId, vehicleType);
                    loadSpots.run();
                    JOptionPane.showMessageDialog(this, "New " + vehicleType + " spot added to Floor " + floorId);
                } else {
                    JOptionPane.showMessageDialog(this, "Insertion cancelled.");
                }


        });


        JButton deleteBtn = new JButton("Delete Selected Spot");
        deleteBtn.addActionListener(e -> {
            String selectedSpot = parkingSpotList.getSelectedValue();
            if (selectedSpot != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selectedSpot + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.deleteSpot(floorId, selectedSpot);
                    loadSpots.run();
                    JOptionPane.showMessageDialog(this, selectedSpot + " has been deleted from Floor " + floorId);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a spot to delete.");
            }


        });

        bottomPanel.add(insertBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(backButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

      
        loadSpots.run();
    }
}