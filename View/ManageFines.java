package View;

import Controller.ParkingFine;
import java.awt.*;
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


        JButton backButton = new JButton("Back to Admin Panel");
        backButton.addActionListener(e -> {
            mainFrame.showPage("AdminPanel");
        });
        add(backButton, BorderLayout.SOUTH);    

    }


   


    private void refreshTableData() {
        tableModel.setRowCount(0); 
        parkingFine.getFineRatesMap().forEach((type, rate) -> {
            tableModel.addRow(new Object[]{type, String.format("%.2f", rate)});
        });
        
    }




    

    
}