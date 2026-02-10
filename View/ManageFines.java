package View;

import Controller.ParkingFine;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageFines extends JPanel {
    
    private JTable fineTable;
    private DefaultTableModel tableModel;
    private ParkingFine parkingFine;

    public ManageFines(MainFrame mainFrame) {
        parkingFine = ParkingFine.getInstance();
        parkingFine.refreshFineRates();
        parkingFine.loadActiveScheme(); // Ensure we have the latest from DB

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] schemes = {
            "Option A : Standard Fines", 
            "Option B : Increased Fines", 
            "Option C : Hourly Stay"
        };

        

        JLabel activeSchemeLabel = new JLabel("Select Active Fine Scheme:");
        activeSchemeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(activeSchemeLabel, BorderLayout.NORTH);


        JComboBox<String> schemeComboBox = new JComboBox<>(schemes);
        schemeComboBox.setSelectedItem(parkingFine.getActiveScheme());

        schemeComboBox.addActionListener(e -> {
            String selectedScheme = (String) schemeComboBox.getSelectedItem();
            if (selectedScheme != null) {
                parkingFine.setActiveScheme(selectedScheme);
                JOptionPane.showMessageDialog(this, "Fine scheme updated to: " + selectedScheme);
            }
        });


        

        // --- Center Section: The Table ---
        String[] columns = {"Fine Type", "Current Rate (RM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JLabel header = new JLabel("Double-click a row to update the fine rate", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        topPanel.add(header);
        topPanel.add(schemeComboBox);
        add(topPanel, BorderLayout.NORTH);

        fineTable = new JTable(tableModel);
        fineTable.setRowHeight(25);
        refreshTableData();

        fineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleUpdate();
                }
            }
        });

        add(new JScrollPane(fineTable), BorderLayout.CENTER);

        // --- Bottom Section: Back Button ---
        JButton backButton = new JButton("Back to Admin Panel");
        backButton.setPreferredSize(new Dimension(0, 40));
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));
        add(backButton, BorderLayout.SOUTH);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0); 
        parkingFine.getFineRatesMap().forEach((type, rate) -> {
            tableModel.addRow(new Object[]{type, String.format("%.2f", rate)});
        });
    }

    private void handleUpdate() {
        int selectedRow = fineTable.getSelectedRow();
        if (selectedRow == -1) return;

        String fineType = (String) tableModel.getValueAt(selectedRow, 0);
        String currentRateStr = (String) tableModel.getValueAt(selectedRow, 1);

        String newRateStr = JOptionPane.showInputDialog(this, 
            "Enter new rate for " + fineType + " (Current: RM " + currentRateStr + "):", 
            "Update Fine Rate", JOptionPane.PLAIN_MESSAGE);
        
        if (newRateStr != null && !newRateStr.trim().isEmpty()) {
            try {
                double newRate = Double.parseDouble(newRateStr);
                if (newRate < 0) throw new NumberFormatException();
                
                parkingFine.updateRate(fineType, newRate);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Fine rate updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}