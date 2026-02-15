package View;

import Controller.Floors;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageParkingComplex extends JPanel {

    private JButton editButton = new JButton("Edit Parking Complex");
    

    public ManageParkingComplex(MainFrame mainFrame) {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("Parking Complex Overview", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26));
        
      
        headerPanel.add(label);
        this.add(headerPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columnNames = {"Floor", "Available", "Occupied" , "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        
        // --- Data Rendering Logic ---
        renderTableData(tableModel);

        this.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton addFloorButton = new JButton("Add New Floor");
        addFloorButton.addActionListener(e -> {
            Floors floors = new Floors(0); 
            String newFloorname = JOptionPane.showInputDialog(this, "Enter new floor name (e.g., 'Level 6'):");
            if (newFloorname == null || newFloorname.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Floor name cannot be empty.");
                return;
            }

            if (floors.addParkingFloor(newFloorname)) {
                renderTableData(tableModel); 
                JOptionPane.showMessageDialog(this, "New floor added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add new floor. Please try again.");
            }
        });

        


        // --- Footer Navigation ---
        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));


        JPanel footerPanel = new JPanel(new FlowLayout()); // Buttons side-by-side
        footerPanel.add(addFloorButton);
        footerPanel.add(backButton);
        this.add(footerPanel, BorderLayout.SOUTH);
     

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        // Change cursor to HAND_CURSOR when hovering over the 'Actions' column (index 3)
        if (col == 3 && row != -1) {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
});

       table.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (col == 3 && row != -1) {
            String floorText = table.getValueAt(row, 0).toString();
            int floorId = Integer.parseInt(floorText.replaceAll("[^0-9]", ""));
            mainFrame.showEditParkingComplex(floorId);
        }
    }
});
    }

    

    private void renderTableData(DefaultTableModel model) {
        // We assume 5 floors as per your previous code snippets
        model.setRowCount(0); // Clear existing rows
        List<Floors> floors = Floors.getFloors();
        for (int i = 1; i <= floors.size(); i++) {
            Floors floorController = new Floors(i); 
            
            int available = floorController.getAvailableSpots(i);
            int occupied = floorController.getOccupiedSpots(i);

            Object[] rowData = {
                "Level " + i,
                available,
                occupied,
                "View Details" 
            };
            model.addRow(rowData);
        }
    }
}