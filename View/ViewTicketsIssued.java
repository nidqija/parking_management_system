package View;
import javax.swing.*;

import Controller.ParkingTicket;
import javax.swing.table.DefaultTableModel;

public class ViewTicketsIssued extends JPanel {
    
    public ViewTicketsIssued(MainFrame mainFrame) {
        

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Tickets Issued");
        label.setAlignmentX(CENTER_ALIGNMENT);
        add(label);

       

        String[] columnNames = {"Ticket ID" , "Number Plate", "Parking Spot", "Entry Time", "Payment Status"};
        Object[][] data = ParkingTicket.getInstance().getTableDataforTicket(); // Replace with actual data retrieval logic
        

        DefaultTableModel model = new DefaultTableModel(data , columnNames);
        JTable ticketTables = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(ticketTables);
        add(scrollPane);

    
        JButton backButton = new JButton("Back to Admin Panel");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));
        add(backButton);

    }
}
