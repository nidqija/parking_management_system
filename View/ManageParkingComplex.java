package View;

import Controller.ParkingComplex;
import Controller.Floors; // Assuming you have a Floors controller for per-floor logic
import java.awt.*;
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
        
        ParkingComplex complex = new ParkingComplex();
        JLabel totalRevenue = new JLabel("Total Estimated Revenue: $" + String.format("%.2f", complex.getFormattedRevenue()), SwingConstants.CENTER);
        totalRevenue.setForeground(new Color(0, 102, 0)); // Dark Green
        
        headerPanel.add(label);
        headerPanel.add(totalRevenue);
        this.add(headerPanel, BorderLayout.NORTH);

        // --- Table Setup ---
        String[] columnNames = {"Floor", "Available", "Occupied" , "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        
        // --- Data Rendering Logic ---
        renderTableData(tableModel);

        this.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Footer Navigation ---
        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));
        this.add(backButton, BorderLayout.SOUTH);

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
        for (int i = 1; i <= 5; i++) {
            Floors floorController = new Floors(i); 
            
            int available = floorController.getAvailableSpots(i);
            int occupied = floorController.getOccupiedSpots(i);

            Object[] rowData = {
                "Floor " + i,
                available,
                occupied,
                "View Details" // This will be the clickable action text
            };
            model.addRow(rowData);
        }
    }
}