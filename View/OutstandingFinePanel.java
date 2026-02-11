package View;
import Controller.Fines;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;




public class OutstandingFinePanel extends JPanel {

   

    public OutstandingFinePanel(MainFrame mainFrame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Outstanding Fines Panel - View Unpaid Fines");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
     
        add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel infoLabel = new JLabel("List of Outstanding Fines:");
        infoLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(infoLabel);

        String [] columnNames = {"License Plate", "Violation Type", "Amount", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable finesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(finesTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        add(scrollPane);


        Controller.Fines finesController = new Controller.Fines();
        
        List<Fines> outstandingFines = finesController.getUnpaidFines();
        
        for (Fines fine : outstandingFines) {
            Object[] rowData = {
                fine.getVehiclePlate(),
                fine.getFineID(),
                fine.getAmount(),
                fine.getStatus()
            };
            tableModel.addRow(rowData);
        }

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));
        add(backButton);
        
    }    
}
