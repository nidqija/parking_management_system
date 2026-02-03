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

        setLayout(new BorderLayout());
        
        String[] columns = {"Fine Type", "Current Rate (RM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        fineTable = new JTable(tableModel);
        refreshTableData();

       

        JLabel header = new JLabel("Double-click a row to update the fine rate", SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(fineTable), BorderLayout.CENTER);

        fineTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked( MouseEvent e){
                  if (e.getClickCount() == 2) {
                        handleUpdate();
                  }
            }
        });

        


        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> {
            mainFrame.showPage("AdminPanel");
        });
        add(backButton, BorderLayout.SOUTH);    

    }



// ============================= Helper Methods =========================================== //

    private void refreshTableData() {
        tableModel.setRowCount(0); 
        parkingFine.getFineRatesMap().forEach((type, rate) -> {
            tableModel.addRow(new Object[]{type, String.format("%.2f", rate)});
        });
        
    }

    private void handleUpdate(){
        int selectedRow = fineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a fine to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fineType = (String) tableModel.getValueAt(selectedRow, 0);
        String currentRateStr = (String) tableModel.getValueAt(selectedRow, 1);

        String newRateStr = JOptionPane.showInputDialog(this, "Enter new rate for " + fineType + " (Current: RM " + currentRateStr + "):", "Update Fine Rate", JOptionPane.PLAIN_MESSAGE);
        
        if (newRateStr != null) {
            try {
                double newRate = Double.parseDouble(newRateStr);
                if (newRate < 0) {
                    throw new NumberFormatException();
                }
                parkingFine.updateRate(fineType, newRate);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Fine rate updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for the rate.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    

    
}